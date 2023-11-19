package Server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.sun.xml.fastinfoset.tools.FI_SAX_Or_XML_SAX_DOM_SAX_SAXEvent;
import java.util.UUID;
import org.example.GenreSearchObject;
import org.example.GenreSearchObject.GenreList;
import org.example.GenreSearchObject.GenreName;
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
    private static int nextSessionId=1; //다음 세션 ID
    private int sessionId; //현재 클라이언트의 세션 ID
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
    public static String userId=null;
    static HashMap<Integer, String> userIdMap = new HashMap<Integer,String>();

    BufferedReader in=null;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        synchronized (ClientHandler.class){
            this.sessionId=nextSessionId++; //세션 ID 할당 및 증가
        }
    }

    public void run(){

        MovieReservationObject movieReservationObject = new MovieReservationObject();
        System.out.println("\tServer :: run()"); //FOR_DEBUG
            //clientHandlers.add(this);  // 여기서 ClientHandler 객체를 추가
        try {
            while(true){
                System.out.println(" ");
                int data = receiveData(socket);

                //System.out.printf("%d에 맞는 메뉴 실행\n",data);

                if(data == LOGIN){  //LOGIN SERVICE
                    System.out.println("\t<<로그인>>");
                    LoginServer login = new LoginServer();
                    login.loginServer(this, socket);
                }
                else if(data == JOIN) {  //회원가입
                    System.out.println("\t<<회원가입>>");
                    JoinServer join = new JoinServer();

                    join.joinServer(socket);
                }
                else if(data==MYINFO){
                    System.out.println("\t<<마이페이지>>");
                    System.out.println("\t --사용자 아이디"+getUserId(getSessionID()));
                    MypageSever_02 myPageServer = new MypageSever_02();

                    Object outputMovieInfo = myPageServer.Mypage(this,getUserId(getSessionID())); //선택한 시간 보내기
                    sendObjectData(socket, MYINFO, outputMovieInfo); //시트 정보 보내기
                }
                else if(data==RESERVATION){ //영화 예매
                    String userID=getUserId(getSessionID());
                    System.out.println("\t<<영화예매>>");
                    MovieReservationServer movieReservationServer=new MovieReservationServer();
                    System.out.println("\t==>영화선택");
                    ArrayList<Pair<Integer,String>>movieName=movieReservationServer.MovieReservationMovieName(this,userID);
                    MovieReservationObject.MovieName movieNameObject=new MovieReservationObject.MovieName(movieName);
                    sendObjectData(socket,5, movieNameObject);

                    int movieId=receiveData(socket);
                    ArrayList movieDate=movieReservationServer.MovieReservationDate(this,movieId,userID);
                    MovieReservationObject.MovieDate movieDateObject=new MovieReservationObject.MovieDate(movieDate);
                    sendObjectData(socket,5, movieDateObject);
                    System.out.println("\t==>날짜선택");
                    byte[] ObjectMovieDate=receiveObjectData(socket);
                    MovieReservationObject.InputMovieDate inputmovieDate=toObject(ObjectMovieDate, InputMovieDate.class);
                    ArrayList outputTime = movieReservationServer.MovieReservationTime(this,movieId,inputmovieDate,userID); //선택한 날짜
                    MovieReservationObject.MovieTime inputMovieTimeObject=new MovieReservationObject.MovieTime(outputTime); //오브젝트 형태로 변환 후 전송
                    sendObjectData(socket, 5, inputMovieTimeObject);
                    System.out.println("\t==>시간선택");
                    byte[] ObjectMovieTime=receiveObjectData(socket);
                    MovieReservationObject.InputMovieTime inputmovieTime=toObject(ObjectMovieTime, InputMovieTime.class);
                    ArrayList<Pair<String, Boolean>> outputSeat = movieReservationServer.MovieReservationSeatCheck(this,movieId,inputmovieDate, inputmovieTime,userID); //선택한 시간 보내기
                    MovieReservationObject.MovieSeat outputMovieSeatObject=new MovieReservationObject.MovieSeat(outputSeat); //오브젝트 형태로 변환 후 전송
                    sendObjectData(socket, 5, outputMovieSeatObject); //시트 정보 보내기
                    System.out.println("\t==>좌석선택");
                    byte[] ObjectMovieSeatNum=receiveObjectData(socket);
                    MovieReservationObject.MovieSeatNum inputmovieSeatNum=toObject(ObjectMovieSeatNum, MovieSeatNum.class);
                    Object outputMovieInfo = movieReservationServer.MovieReservationInfo(this,movieId,inputmovieDate, inputmovieTime, inputmovieSeatNum,userID); //선택한 시간 보내기
                    sendObjectData(socket, 5, outputMovieInfo); //시트 정보 보내기
                    System.out.println("\t==>정보확인");
                    int infoCheck=receiveData(socket);
                    int reservationCheck = movieReservationServer.MovieReservation(this,movieId,inputmovieDate, inputmovieTime, inputmovieSeatNum, infoCheck, userID);
                    IsOK = 1; //이거 다시해야할듯요 reservationCheck가 1이면 예매 된거고 0 혹은 3이면 예매 안된걸루
                    sendData(socket, 5, reservationCheck); //예매확인
                }
                else if(data==GENRE){
                    System.out.println("\t<<장르검색>>");
                    GenreSearchServer genreSearchServer=new GenreSearchServer();

                    ArrayList<Pair<Integer,String>> genreList=genreSearchServer.getDistinctGenres(this);
                    GenreSearchObject.GenreList genreListObject=new GenreSearchObject.GenreList(genreList);
                    sendObjectData(socket, GENRE, genreListObject);

                    byte[] ObjectGenreNumObject=receiveObjectData(socket);
                    GenreSearchObject.GenreName MovieNum=toObject(ObjectGenreNumObject, GenreName.class);
                    ArrayList<String> outputMovieNum=genreSearchServer.searchMoviesByGenre(this,MovieNum);
                    GenreSearchObject.GenreMovieName outputMovieNumObject=new GenreSearchObject.GenreMovieName(outputMovieNum);
                    sendObjectData(socket,GENRE,outputMovieNumObject);
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
    public int getSessionID(){
        return sessionId;
    }

    public static synchronized void addUserId(int sessionId, String userId) {
        userIdMap.put(sessionId, userId);
    }

    public static synchronized String getUserId(int sessionId) {
        return userIdMap.get(sessionId);
    }

    public static void sendObjectData(Socket socket, int menuNum,Object obj) throws IOException {
        System.out.println("\tServer :: sendObjectData() ::");   //FOR DEBUG

        DataType=1;
        IsData=1;

        //생성한 객체를 byte array로 변환
        byte[] objectData = toByteArray(obj);   //앞에 2byte엔 헤더 붙여야됨
        //System.out.printf("Object data size: 0x%x\n",objectData.length); // 객체 사이즈 출력
        // 새로운 바이트 배열을 생성 (헤더 크기 + 객체 데이터 크기)
        byte[] dataWithHeader =  new byte[2 + objectData.length];

        int header=0;
        header = parseData_en(menuNum);
        //System.out.printf("header: 0x%x\n",header);
        byte[] headerArr = new byte[2];

        headerArr[0] = (byte) (header >> 8);       // 상위 8비트
        headerArr[1] = (byte) (header);            // 하위 8비트

        System.arraycopy(headerArr,0, dataWithHeader,0, headerArr.length);
        System.arraycopy(objectData,0, dataWithHeader,headerArr.length, objectData.length);

        //System.out.printf("[0]: 0x%x, [1]: 0x%x\n",dataWithHeader[0],dataWithHeader[1]);

//        for(int i = 0; i < 2 + objectData.length;i++)
//        {
//            System.out.printf("dataWithHeader[%d]: 0x%x\n",i, dataWithHeader[i]);
//        }
        //System.out.printf("dataWithHeader data size: 0x%x\n",dataWithHeader.length); // 객체 사이즈 출력

        //서버로 내보내기 위한 출력 스트림 뚫음
        OutputStream os = socket.getOutputStream();
        //출력 스트림에 데이터 씀
        //os.write(objectData);
        os.write(dataWithHeader);
        //보냄
        os.flush();
        IsOK = 0;
    }
    //send Object


    ////////ToByteArray////////////
    public static byte[] toByteArray (Object obj)
    {
        //System.out.println("Server :: toByteArray() ::");   //FOR DEBUG
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            //System.out.println("Received object type: " + obj.getClass().getName());
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
        System.out.println("\tServer :: receiveObject() ::");   //FOR DEBUG
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
        //System.out.printf("buffer[0]: 0x%x, buffer[1]: 0x%x\n", recvBuffer[0],recvBuffer[1]);
        //System.out.printf("Header: 0x%x\n", header);
        parseData_de(2,header);
        // 나머지 데이터 복사
        byte[] objectData = new byte[nReadSize - 2];
        System.arraycopy(recvBuffer, 2, objectData, 0, nReadSize - 2);

        //System.out.printf("nReadSize: 0x%x\n",nReadSize);

        //받아온 값이 0보다 클때 = 받은 값이 존재할 때
        if (nReadSize > 0) {
            return objectData;
        }
        return null;
    }


    ////ToObject()/////////////////
    public static <T> T toObject (byte[] bytes, Class<T> type)
    {
        //System.out.println("\nSever :: toObject() ::");   //FOR DEBUG
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
        }
        catch (IOException ex) {
            //TODO: Handle the exception
            System.out.println("IOException: " + ex.getMessage());
            ex.printStackTrace(); // 이 부분을 추가하여 오류의 상세 내용을 출력
        }
        catch (ClassNotFoundException ex) {
            //TODO: Handle the exception
        }
        return type.cast(obj);
    }


    //send Byte
    public static void sendData(Socket socket,int menuNum,int data)throws IOException{
        System.out.println("\tServer :: sendData() ::");

        OutputStream os = socket.getOutputStream();

        int header=0;

        DataType = 0;
        if(data != 0)
            IsData = 1;
        else
            IsData = 0;

        header = parseData_en(menuNum) << 16;
        //System.out.printf("header: 0x%x\n",header);

        data |= header;

        System.out.printf("\tdata: 0x%x\n",data);
        // 4바이트 크기의 버퍼를 생성합니다.
        byte[] buffer = new byte[4];

        buffer[0] = (byte) (data >> 24);
        buffer[1] = (byte) (data >> 16);
        buffer[2] = (byte) (data >> 8);
        buffer[3] = (byte) (data);

        // 바이트 배열을 스트림을 통해 전송합니다.
        os.write(buffer);
        os.flush(); // 버퍼에 남아있는 데이터를 모두 전송합니다.
        IsOK = 0;
    }

    public static int receiveData(Socket socket) throws IOException{
        System.out.printf("\tSever:: receiveData() ::");
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

        return recvData;

    }
    static int parseData_de(int dataType, int value)
    {
        System.out.print("\tServer :: parseData_de()");
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

        System.out.printf("\tnt: %x, iE: %x, eC: %x, iD: %x, dT: %x, mN: %x, iN: %x, iO:%x\n"
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
        System.out.printf("\tServer :: parseData_en() :: header: 0x%x\n",header);
        return header;

    }
    private void disconnect() { //----- == 로그아웃
        System.out.println("\nServer :: disconnect()"); //FOR_DEBUG
        clientHandlers.remove(this);
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Socket close error: " + e.getMessage());
        }
    }
}
