package org.example;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
    //상수
    private static final int SEND = 1;
    private static final int RECV = 2;

    private static final String NULL_STRING_DATA = " ";
    private static final int LOGIN = 1;
    private static final int JOIN = 2;
    private static final int RESERVATION = 5;
    private static final int GENRE = 6;
    private static final int MYINFO = 8;
    static int C_networkType = 0;
    static int C_isError = 0;
    static int C_errorCode = 0;
    static int C_isData = 0;
    static int C_dataType = 0;  //0: 4byte, 1: Object
    static int C_menuNum = 0;
    static int C_idNum = 0;
    static int C_isOK = 0;

    //FOR TEST
    static short HEADER_CODE = 0;

    private static Scanner sc = new Scanner(System.in); //-----이걸 왜 프라이빗??

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println("Client Class: main()"); //FOR_DEBUG
        String hostname = "localhost";
        int port = 1717;

        try(Socket socket = new Socket(hostname, port)) { //소켓 연결
            System.out.println("Connected to Server");

            loginPage(socket);
        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        }


    }
    public static void loadMainMenu(){
        int num = 0;
        //Scanner sc = new Scanner(System.in);
        Manager M = new Manager(); //-----매니저 클래스가 정확하게 뭔지...

        do {
            try {
                sc = new Scanner(System.in); //-----?? 이걸 왜 다시 선언하나용?
                //메인 선택창
                M.displayMainMenu();
                System.out.printf("번호를 입력해 주세요: ");

                num = sc.nextInt();

                if(num > 4) {
                    System.out.println("잘못 입력했습니다. 다시 입력해주세요");
                    continue;
                }
                switch (num) {
                    case 1:	//예매
                        System.out.println("\n<< 영화 예매 >>\n");
                        M.ticket();
                        break;
                    case 2:	//추천
                        System.out.println("\n<< 영화 추천 >>\n");
                        M.displayGenreList();
                        M.recommand();
                        break;
                    case 3:	//확인
                        System.out.println("\n<< 예매 확인 >>\n");
                        M.confirm();
                        break;
                    default:	//끝
                        break;
                }
            }catch(InputMismatchException e){
                System.out.println("잘못 입력했습니다.");
            }
        }while(num != 4); //-----이게 이 반복이 뭐지....?? num이 4가 아닐동안 반복? -> break로만 종료하게??

        System.out.println("<< 이용해 주셔서 감사합니다 >>");
    }


    public static void loginPage(Socket socket) throws IOException {
        sc = new Scanner(System.in);

        int menuNum = 3;
        System.out.println("Here is Login page!!"); //나중에 지우기
        System.out.println("1. 로그인");
        System.out.println("2. 회원가입");
        System.out.print("입력하세요 : ");

        while(true){ //-----이게 좋지 않을까...
            menuNum=sc.nextInt();
            if(menuNum==1||menuNum==2){
                break;
            }
            else{
                System.out.println("잘못 입력했습니다. 다시 입력해주세요.");
            }
        }
//        try {
//            String input = sc.nextLine(); // 문자열로 입력 받음
//            menuNum = Integer.parseInt(input); // 문자열을 숫자로 변환
//        } catch (NumberFormatException e) {
//            System.out.println("숫자로 입력해야 합니다.");
//            return;
//        }

        switch (menuNum){
            case 1:
                //서버에 로그인 정보가 갈꺼라고 send
                sendData(socket,LOGIN,1);

                //receive
                int data = receiveData(socket);
                if(C_isOK == 1){ //-----이거 다 테스트용인거죠? 지울거죠??

                    //서버에서 알겠다고 ok 오면 send
                    System.out.print("아이디: ");
//                    String id = sc.nextLine();
                    String id = sc.next(); //-----아이디랑 비밀번호에는 공백이 없으니 next로 받아도될듯해요
                    System.out.print("비밀번호: ");
//                    String pw = sc.nextLine();
                    String pw = sc.next();

                    Login.LoginInfo logInfo = new Login.LoginInfo(id, pw);

                    sendObjectData(socket,LOGIN,logInfo);
                    receiveData(socket);

                    final int idNum = C_idNum;
                    System.out.printf("idNum: %d\n",idNum);

                    if(C_menuNum >= 4) //-----이게 뭐죠?? 이상한 숫자가 들어오면 그냥 메인페이지 글자 다시 출력인지요?
                        loadMainMenu();
                }
                break;
            case 2:
                break;
        }

    }
    //send Object
    public static void sendObjectData(Socket socket, int menuNum,Object obj) throws IOException {
        System.out.println("Client :: sendObjectData() ::");   //FOR DEBUG
        //Person 객체 생성. 인자로 3 넣어줌. ----- 이건 뭐 없는거같은데 객체생성이 어디잇는지.....

        //생성한 객체를 byte array로 변환
        byte[] objectData = toByteArray(obj);   //앞에 2byte엔 헤더 붙여야됨 -----2byte == 16bit
        //System.out.printf("Object data size: 0x%x\n",objectData.length); // 객체 사이즈 출력
        // 새로운 바이트 배열을 생성 (헤더 크기 + 객체 데이터 크기)
        byte[] dataWithHeader =  new byte[2 + objectData.length];

        int header=0;
        HEADER_CODE = 0x640; //-----어디에 해당하는 헤더코드?? 이게 0이네용
        header = parseData_en(menuNum);
        System.out.printf("header: 0x%x\n",header);
        byte[] headerArr = new byte[2];

        headerArr[0] = (byte) (header >> 8);       // 상위 8비트
        headerArr[1] = (byte) (header);            // 하위 8비트

        System.arraycopy(headerArr,0, dataWithHeader,0, headerArr.length);
        System.arraycopy(objectData,0, dataWithHeader,headerArr.length, objectData.length);

        System.out.printf("[0]: 0x%x, [1]: 0x%x\n",dataWithHeader[0],dataWithHeader[1]);

//        for(int i = 0; i < 2 + objectData.length;i++)
//        {
//            System.out.printf("dataWithHeader[%d]: 0x%x\n",i, dataWithHeader[i]);
//        }
        System.out.printf("dataWithHeader data size: 0x%x\n",dataWithHeader.length); // 객체 사이즈 출력

        //서버로 내보내기 위한 출력 스트림 뚫음
        OutputStream os = socket.getOutputStream();
        //출력 스트림에 데이터 씀
        //os.write(objectData);
        os.write(dataWithHeader);
        //보냄
        os.flush();
    }

    ////////ToByteArray////////////
    public static byte[] toByteArray (Object obj)
    {
        System.out.println("Client :: toByteArray() ::");   //FOR DEBUG
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

            Login.LoginInfo loginInfo = toObject(objectData,Login.LoginInfo.class); //-----굿노트에 올린 질문...
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
        HEADER_CODE = 0x440; //-----0x == 16진수 (0b == 2진수)
        header = parseData_en(menuNum) << 16;
        System.out.printf("header: 0x%x\n",header);

        data |= header; //-----데이터에 헤더를 붙이는??

        System.out.printf("data: 0x%x\n",data);
        // 4바이트 크기의 버퍼를 생성합니다.
        byte[] buffer = new byte[4];

        buffer[0] = (byte) (data >> 24); //-----이게 뭔지요??
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
        //dataType = 1 => 헤더, 데이터 모두 전달 //------ 1이면 데이터가 존재한다는 의미인지요...
        //dataType = 2 => 헤더 값만 전달
        short header = 0; //----- 2byte == 16 bits
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

        C_networkType = (header >> 15) & 0x01;
        C_isError = (header >> 14) & 0x01;
        C_errorCode = (header >> 11) & 0x07;
        C_isData = (header >> 10) & 0x01;
        C_dataType = (header >> 9) & 0x01;
        C_menuNum = (header >> 6) & 0x07;
        C_idNum = (header >> 1)& 0x1F;
        C_isOK = header & 0x1;

        System.out.printf("nt: %x, iE: %x, eC: %x, iD: %x, dT: %x, mN: %x, iN: %x, iO:%x\n"
                ,C_networkType,C_isError,C_errorCode,C_isData
                ,C_dataType,C_menuNum,C_idNum,C_isOK);
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
        //Login menu : 0x440;

        enValue = HEADER_CODE;//0x440;//0b0000010001000000;

        return enValue;

    }
}
