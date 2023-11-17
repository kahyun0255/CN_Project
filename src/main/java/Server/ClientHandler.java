package Server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.example.Login;
import org.example.Login.LoginInfo;
import org.example.MovieReservationObject;
import org.example.MovieReservationObject.InputMovieDate;
import org.example.MovieReservationObject.InputMovieTime;
import org.example.MovieReservationObject.MovieDate;
import org.example.MovieReservationObject.MovieName;
import org.example.MovieReservationObject.MovieSeatNum;
import org.example.Pair;


public class ClientHandler extends Thread {
    public Socket socket;
    private String name;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static final String NULL_STRING_DATA = " ";
    public static final int LOGIN = 1;
    public static final int JOIN = 2;
    public static final int RESERVATION = 5;
    public static final int GENRE = 6;
    public static final int MYINFO = 8;

    public static int NetworkType = 0;
    public static int IsError = 0;
    public static int ErrorCode = 0;
    public static int IsData = 0;
    public static int DataType = 0;
    public static int MenuNum = 0;
    public static int IdNum = 0;
    public static int IsOK = 0;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run(){
        MovieReservationObject movieReservationObject = new MovieReservationObject();
        System.out.println("Server :: run()"); //FOR_DEBUG
            //clientHandlers.add(this);  // 여기서 ClientHandler 객체를 추가
        try {
            while(true){
                int data = receiveData(socket);
                System.out.println("1\n");
                System.out.printf("%d에 맞는 메뉴 실행\n",data);

                if(data == 1){  //LOGIN SERVICE
                    LoginServer login = new LoginServer();

                    login.loginServer(socket);

                }
                else if(data == 2) {  //회원가입
                    //DB에서 데이터 받아옴
                    IsOK = 1;
                    sendData(socket,JOIN,0);
                    receiveObjectData(socket);

                    //받은 정보 DB에 저장

                    //저장완료되면 ok 사인 보내기
                    IsOK = 1;
                    sendData(socket,JOIN,0);

                }

                else if(data==5){ //영화 예매
                    MovieReservationServer movieReservationServer=new MovieReservationServer();

                    ArrayList<Pair<Integer,String>>movieName=movieReservationServer.MovieReservationMovieName(this);
                    MovieReservationObject.MovieName movieNameObject=new MovieReservationObject.MovieName(movieName);
                    sendObjectData(socket,5, movieNameObject);
                    //영화 목록 보내기 성공

                    int movieId=receiveData(socket);
                    ArrayList movieDate=movieReservationServer.MovieReservationDate(this,movieId);
                    MovieReservationObject.MovieDate movieDateObject=new MovieReservationObject.MovieDate(movieDate);
                    sendObjectData(socket,5, movieDateObject);

                    byte[] ObjectMovieDate=receiveObjectData(socket);
                    MovieReservationObject.InputMovieDate inputmovieDate=toObject(ObjectMovieDate, InputMovieDate.class);
                    ArrayList outputTime = movieReservationServer.MovieReservationTime(this,movieId,inputmovieDate); //선택한 날짜
                    MovieReservationObject.MovieTime inputMovieTimeObject=new MovieReservationObject.MovieTime(outputTime); //오브젝트 형태로 변환 후 전송
                    sendObjectData(socket, 5, inputMovieTimeObject);

                    byte[] ObjectMovieTime=receiveObjectData(socket);
                    MovieReservationObject.InputMovieTime inputmovieTime=toObject(ObjectMovieTime, InputMovieTime.class);
                    ArrayList<Pair<String, Boolean>> outputSeat = movieReservationServer.MovieReservationSeatCheck(this,movieId,inputmovieDate, inputmovieTime); //선택한 시간 보내기
                    MovieReservationObject.MovieSeat outputMovieSeatObject=new MovieReservationObject.MovieSeat(outputSeat); //오브젝트 형태로 변환 후 전송
                    sendObjectData(socket, 5, outputMovieSeatObject); //시트 정보 보내기

                    byte[] ObjectMovieSeatNum=receiveObjectData(socket);
                    MovieReservationObject.MovieSeatNum inputmovieSeatNum=toObject(ObjectMovieSeatNum, MovieSeatNum.class);
                    Object outputMovieInfo = movieReservationServer.MovieReservationInfo(this,movieId,inputmovieDate, inputmovieTime, inputmovieSeatNum); //선택한 시간 보내기
                    sendObjectData(socket, 5, outputMovieInfo); //시트 정보 보내기

                    int infoCheck=receiveData(socket);
                    movieReservationServer.MovieReservation(this,movieId,inputmovieDate, inputmovieTime, inputmovieSeatNum, infoCheck);
                }
                else if(data==9){
                    break;
                }
                else{
                    sendData(socket,LOGIN,0);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally{
            disconnect();
        }
    }
    public static void sendObjectData(Socket socket, int menuNum,Object obj) throws IOException {
        System.out.println("\nServer :: sendObjectData() ::");   //FOR DEBUG
        //Person 객체 생성. 인자로 3 넣어줌.

        DataType = 1;
        IsData = 1;

        //생성한 객체를 byte array로 변환
        byte[] objectData = toByteArray(obj);   //앞에 2byte엔 헤더 붙여야됨
        System.out.printf("Object data size: 0x%x\n",objectData.length); // 객체 사이즈 출력
        // 새로운 바이트 배열을 생성 (헤더 크기 + 객체 데이터 크기)
        byte[] dataWithHeader =  new byte[2 + objectData.length];

        int header=0;

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
    //send Object


    ////////ToByteArray////////////
    public static byte[] toByteArray (Object obj)
    {
        System.out.println("Server :: toByteArray() ::");   //FOR DEBUG
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            System.out.println("Received object type: " + obj.getClass().getName());
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
    public static byte[] receiveObjectData(Socket socket) throws IOException {
        System.out.println("\nServer :: receiveObject() ::");   //FOR DEBUG
        //수신 버퍼의 최대 사이즈 지정
        int maxBufferSize = 2048;
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
//            // 역직렬화를 위해 toObject 메소드 수정 필요
//
//            Login.LoginInfo loginInfo = toObject(objectData,Login.LoginInfo.class);

//            // 객체 사용
//            System.out.println("Received ID: " + loginInfo.id);
//            System.out.println("Received PW: " + loginInfo.pw);
          return objectData;
        }
        return null;
    }


    ////ToObject()/////////////////
    public static <T> T toObject (byte[] bytes, Class<T> type)
    {
        System.out.println("\nSever :: toObject() ::");   //FOR DEBUG
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
            System.out.println("IOException: " + ex.getMessage());
            ex.printStackTrace(); // 이 부분을 추가하여 오류의 상세 내용을 출력
        }
        catch (ClassNotFoundException ex) {
            //TODO: Handle the exception
            //System.out.println("&&&&&&&");
        }

        return type.cast(obj);
    }


    //send Byte
    public static void sendData(Socket socket,int menuNum,int data)throws IOException{

        System.out.println("\nServer :: sendData() ::");
        OutputStream os = socket.getOutputStream();

        int header=0;

        DataType = 0;
        if(data != 0)
            IsData = 1;
        else
            IsData = 0;

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
        System.out.println("\nSever:: receiveData()");
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
    static int parseData_de(int dataType, int value)
    {
        System.out.print("\nServer :: parseData_de()");
        //dataType = 1 => 헤더, 데이터 모두 전달
        //dataType = 2 => 헤더 값만 전달
        short header = 0;//
        int data = 0;

        if(dataType == 1) {
            header = (short)(value >> 16) ;
            data = value & 0xFF;
        }
        else if(dataType == 2){
            header = (short)value;
        }

        System.out.printf(" h: 0x%x, d: 0x%x\n",header, data);

        NetworkType = (header >> 15) & 0x01;
        IsError = (header >> 14) & 0x01;
        ErrorCode = (header >> 11) & 0x07;
        IsData = (header >> 10) & 0x01;
        DataType = (header >> 9) & 0x01;
        MenuNum = (header >> 6) & 0x07;
        IdNum = (header >> 1)& 0x1F;
        IsOK = header & 0x1;

        System.out.printf("nt: %x, iE: %x, eC: %x, iD: %x, dT: %x, mN: %x, iN: %x, iO:%x\n"
                ,NetworkType,IsError,ErrorCode,IsData,DataType,MenuNum,IdNum,IsOK);

        if(dataType == 1) {
            return data;
        }
        else {
            return 0;
        }
    }
    static short parseData_en(int menuNum)
    {
        System.out.println("\nServer :: parseData_en()");
        //type 1: Send, 2: Receive
        //menuNum:
        // 1: login, 2: join, 5: reservation, 6: recommand, 8: myInfo

        short header = 0;
        //TEST
        //Login menu : 0x440;
        NetworkType = 1;
        MenuNum = menuNum;

        header = (short)(((NetworkType << 15) & 0x8000)|((IsError << 14) & 0x6000)|
                ((ErrorCode << 11) & 0x3800)|((IsData << 10) & 0x600)|
                ((DataType << 9) & 0x200)|((MenuNum << 6) & 0x1C0)|
                ((IdNum << 1) & 0x3E)|(IsOK  & 0x1));

        System.out.printf("Server :: parseData_en() :: header: 0x%x\n",header);

        return header;

    }

    private void disconnect() { //----- == 로그아웃
        System.out.println("\nServer :: disconnect()"); //FOR_DEBUG
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
