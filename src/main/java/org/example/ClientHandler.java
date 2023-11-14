package org.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ClientHandler extends Thread {
    private Socket socket;
    private String name;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    private static final int SEND = 1;
    private static final int RECV = 2;

    private static final String NULL_STRING_DATA = " ";
    private static final int LOGIN = 1;
    private static final int JOIN = 2;
    private static final int RESERVATION = 5;
    private static final int GENRE = 6;
    private static final int MYINFO = 8;

    static int networkType = 0;
    static int isError = 0;
    static int errorCode = 0;
    static int isData = 0;
    static int dataType = 0;
    static int menuNum = 0;
    static int idNum = 0;
    static int isOK = 0;
    static short HEADER_CODE = 0;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run(){
        System.out.println("ClientHandler Class: run()"); //FOR_DEBUG
            //clientHandlers.add(this);  // 여기서 ClientHandler 객체를 추가
        while (true) {
            try {
                int data = receiveData(socket);

                System.out.printf("%d에 맞는 메뉴 실행\n",data);
                if(data == 1){  //LOGIN SERVICE
                    HEADER_CODE =(short)0x8041;
                    sendData(socket,LOGIN,0);
                    receiveObjectData(socket);
                    //받은 데이터 값을 DB에서 비교 후
                    HEADER_CODE =(short)0x8102;
                    sendData(socket,LOGIN,0);
                }
                else{
                    sendData(socket,LOGIN,0);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //send Object
    public static void sendObjectData(Socket socket, int menuNum,Object obj) throws IOException {
        System.out.println("Server :: sendObjectData() ::");   //FOR DEBUG
        //Person 객체 생성. 인자로 3 넣어줌.

        //생성한 객체를 byte array로 변환
        byte[] objectData = toByteArray(obj);   //앞에 2byte엔 헤더 붙여야됨

        // 새로운 바이트 배열을 생성 (헤더 크기 + 객체 데이터 크기)
        byte[] dataWithHeader =  new byte[2 + objectData.length];

        int header=0;
        header = parseData_en(menuNum);
        System.out.printf("header: 0x%x\n",header);

        // 헤더 설정 (예: menuNum 값을 헤더로 사용)
        dataWithHeader[0] = (byte) (header >> 8);       // 상위 8비트
        dataWithHeader[1] = (byte) (header);            // 하위 8비트

        //서버로 내보내기 위한 출력 스트림 뚫음
        OutputStream os = socket.getOutputStream();
        //출력 스트림에 데이터 씀
        os.write(dataWithHeader);
        //보냄
        os.flush();
    }

    ////////ToByteArray////////////
    public static byte[] toByteArray (Object obj)
    {
        System.out.println("Server :: toByteArray() ::");   //FOR DEBUG
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            bos.close();
            bytes = bos.toByteArray();
        }
        catch (IOException ex) {
            //TODO: Handle the exception
        }
        return bytes;
    }
    ///////////RECEIVE/////////////////
    public static void receiveObjectData(Socket socket) throws IOException {
        System.out.println("Server :: receiveObject() ::");   //FOR DEBUG
        //수신 버퍼의 최대 사이즈 지정
        int maxBufferSize = 1024;
        //버퍼 생성
        byte[] recvBuffer = new byte[maxBufferSize];
        //서버로부터 받기 위한 입력 스트림 뚫음
        InputStream is = socket.getInputStream();
        //버퍼(recvBuffer) 인자로 넣어서 받음. 반환 값은 받아온 size
        int nReadSize = is.read(recvBuffer);

//        for(int i = 0; i < nReadSize; i++)
//        {
//            System.out.printf("recvB[%d]: 0x%x ",i, recvBuffer[i]);
//        }
        // 헤더 추출
        int header = ((recvBuffer[0] & 0xFF) << 8) | (recvBuffer[1] & 0xFF);
        System.out.printf("buffer[0]: 0x%x, buffer[1]: 0x%x\n", recvBuffer[0],recvBuffer[1]);
        //System.out.printf("Header: 0x%x\n", header);
        parseData_de(2,header);
        // 나머지 데이터 복사
        byte[] objectData = new byte[nReadSize - 2];
        System.arraycopy(recvBuffer, 2, objectData, 0, nReadSize - 2);

        System.out.printf("nReadSize: 0x%x\n",nReadSize);

        //받아온 값이 0보다 클때 = 받은 값이 존재할 때
        if (nReadSize > 0) {
            // 역직렬화를 위해 toObject 메소드 수정 필요

            Login.LoginInfo loginInfo = toObject(objectData,Login.LoginInfo.class);
            //Login.LoginInfo loginInfo = toObject(recvBuffer,Login.LoginInfo.class);
            // 객체 사용
            System.out.println("Received ID: " + loginInfo.id);
            System.out.println("Received PW: " + loginInfo.pw);

        }
    }


    ////ToObject()/////////////////
    public static <T> T toObject (byte[] bytes, Class<T> type)
    {
        System.out.println("Sever :: toObject() ::");   //FOR DEBUG
        Object obj = null;
        //System.out.println("@@@@@@@");
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            //System.out.println("!!!!!!!!");
        }
        catch (IOException ex) {
            //TODO: Handle the exception
            //System.out.println("$$$$$$$$");
            //System.out.println("IOException: " + ex.getMessage());
            ex.printStackTrace(); // 이 부분을 추가하여 오류의 상세 내용을 출력
        }
        catch (ClassNotFoundException ex) {
            //TODO: Handle the exception
            //System.out.println("&&&&&&&");
        }
        System.out.println("?????????");
        return type.cast(obj);
    }


    //send Byte
    public static void sendData(Socket socket,int menuNum,int data)throws IOException{

        OutputStream os = socket.getOutputStream();

        int header=0;
        header = parseData_en(menuNum) << 16;
        System.out.printf("header: 0x%x\n",header);

        data |= header;

        System.out.printf("data: 0x%x\n",data);
        // 4바이트 크기의 버퍼를 생성합니다.
        byte[] buffer = new byte[4];

        buffer[0] = (byte) (data >> 24);
        buffer[1] = (byte) (data >> 16);
        buffer[2] = (byte) (data >> 8);
        buffer[3] = (byte) (data);

        // 바이트 배열을 스트림을 통해 전송합니다.
        os.write(buffer);
        os.flush(); // 버퍼에 남아있는 데이터를 모두 전송합니다.
    }

    public static int receiveData(Socket socket) throws IOException{
        System.out.println("Sever:: receiveData() ::");
        //서버 통신
        InputStream is = socket.getInputStream();

        while(is.available() < 4){
            try {
                // 데이터가 준비되길 기다립니다.
                Thread.sleep(100); // 100 밀리초 대기
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for data: " + e.getMessage());
                return 0;
            }
        }
        // 4바이트 크기의 버퍼를 생성합니다.
        byte[] buffer = new byte[4];
        int bytesRead = 0;

        // 정확히 4바이트를 읽을 때까지 반복합니다.
        while (bytesRead < 4) {
            int result = is.read(buffer, bytesRead, 4 - bytesRead);
            if (result == -1) break; // 스트림의 끝에 도달했을 경우
            bytesRead += result;
        }

        // 읽은 바이트 배열을 정수로 변환합니다.
        int value = (buffer[0] << 24) & 0xff000000 |
                (buffer[1] << 16) & 0x00ff0000 |
                (buffer[2] << 8)  & 0x0000ff00 |
                (buffer[3])       & 0x000000ff;

        System.out.printf("Received integer: 0x%x\n",value);

        int recvData = parseData_de(1,value);
        System.out.printf("recvData: 0x%x\n",recvData);
        return recvData;

    }
    static int parseData_de(int DataType, int value)
    {
        //dataType = 1 => 헤더, 데이터 모두 전달
        //dataType = 2 => 헤더 값만 전달
        short header = 0;//
        int data = 0;

        if(DataType == 1) {
            System.out.println("dataType 1\n");
            header = (short)(value >> 16) ;
            data = value & 0xFF;
        }
        else if(DataType == 2){
            header = (short)value;
        }
        System.out.printf("h: 0x%x, d: 0x%x\n",header, data);

        networkType = (header >> 15) & 0x01;
        isError = (header >> 14) & 0x01;
        errorCode = (header >> 11) & 0x07;
        isData = (header >> 10) & 0x01;
        dataType = (header >> 9) & 0x01;
        menuNum = (header >> 6) & 0x07;
        idNum = (header >> 1)& 0x1F;
        isOK = header & 0x1;

        System.out.printf("nt: %x, iE: %x, eC: %x, iD: %x, dT: %x, mN: %x, iN: %x, iO:%x\n"
                    ,networkType,isError,errorCode,isData,dataType,menuNum,idNum,isOK);
        if(DataType == 1) {
            return data;
        }
        else {
            return 0;
        }
    }
    static short parseData_en(int menuNum)
    {
        //type 1: Send, 2: Receive
        //menuNum:
        // 1: login, 2: join, 5: reservation, 6: recommand, 8: myInfo

        short enValue = 0;
        //TEST
        enValue = (short)HEADER_CODE;

        return enValue;

    }
    private void disconnect() { //----- == 로그아웃
        System.out.println("ClientHandler Class: disconnect()"); //FOR_DEBUG
        if (name != null) {
            clientHandlers.remove(this);
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Socket close error: " + e.getMessage());
        }
    }
}
