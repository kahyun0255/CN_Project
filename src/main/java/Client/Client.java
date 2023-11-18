package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
//import org.example.GenreSearchObject;
//import org.example.GenreSearchObject.GenreList;
//import org.example.GenreSearchObject.GenreMovieName;
//import org.example.Login;
//import org.example.MovieReservationObject;
//import org.example.MovieReservationObject.MovieDate;
//import org.example.MovieReservationObject.MovieInfo;
//import org.example.MovieReservationObject.MovieName;
//import org.example.MovieReservationObject.MovieSeat;
//import org.example.MovieReservationObject.MovieSeatNum;
//import org.example.MovieReservationObject.MovieTime;
//import org.example.MyPageObject;
//import org.example.MyPageObject.MyPageInfo;
//import org.example.Pair;

import org.example.*;
import org.example.GenreSearchObject.GenreList;
import org.example.GenreSearchObject.GenreMovieName;
import org.example.MovieReservationObject.MovieDate;
import org.example.MovieReservationObject.MovieInfo;
import org.example.MovieReservationObject.MovieName;
import org.example.MovieReservationObject.MovieSeat;
import org.example.MovieReservationObject.MovieTime;
import org.example.MyPageObject.MyPageInfo;

public class Client {
    public Socket socket;
    //상수
    public Client(Socket socket){
        this.socket=socket;
    }

    public static final int LOGIN = 1;
    public static final int JOIN = 2;
    public static final int RESERVATION = 5;
    public static final int GENRE = 6;
    public static final int MYINFO = 8;
    public static int C_networkType = 0;
    public static int C_isError = 0;
    public static int C_errorCode = 0;
    public static int C_isData = 0;
    public static int C_dataType = 0;  //0: 4byte, 1: Object
    public static int C_menuNum = 0;
    public static int C_idNum = 0;
    public static int C_isOK = 0;

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        LoginClient Login = new LoginClient();
        System.out.println("Client :: main()"); //FOR_DEBUG
        String hostname = "localhost";
        int port = 7778;

        try {
            Socket socket=new Socket(hostname, port);
            System.out.println("Connected to Server");
            Client clinet = new Client(socket);


            clinet.loadMainMenu(socket);
//            Login.loginPage(socket);

        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }

//    public void loginPage(Socket socket) throws IOException {
//
//        System.out.println("\nClient :: loginPage()"); //FOR_DEBUG
//        sc = new Scanner(System.in);
//
//        int menuNum = 0;
//
//        System.out.println("1. 로그인");
//        System.out.println("2. 회원가입");
//        System.out.print("입력하세요: ");
//
//        //입력값 체크
//        while(true){
//            menuNum=sc.nextInt();
//            if(menuNum>2){
//                System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.");
//            }
//            else{
//                break;
//            }
//        }
//
//        if(menuNum==1) {
//            //서버에 로그인 정보가 갈꺼라고 send
//            sendData(socket, LOGIN, 1);
//    public void loginPage(Socket socket) throws IOException {
//
//        System.out.println("\nClient :: loginPage()"); //FOR_DEBUG
//        sc = new Scanner(System.in);
//
//        int menuNum = 0;
//
//        System.out.println("1. 로그인");
//        System.out.println("2. 회원가입");
//        System.out.print("입력하세요: ");
//
//        //입력값 체크
//        while(true){
//            menuNum=sc.nextInt();
//            if(menuNum>2){
//                System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.");
//            }
//            else{
//                break;
//            }
//        }
//
//        if(menuNum==1) {
//            //서버에 로그인 정보가 갈꺼라고 send
//            sendData(socket, LOGIN, 1);
//
//            }
//        }
//        else if(menuNum==2) {
//            System.out.println("회원가입 페이지 입니다.");
//
//            sendData(socket,JOIN,2);
//
//            receiveData(socket);
//            if(C_isOK == 1){
//                System.out.print("이름: ");
//                String j_name = sc.next();
//                System.out.print("아이디: ");
//                String j_id = sc.next();
//                System.out.print("비밀번호: ");
//                String j_pw = sc.next();
//                C_isOK = 0;
//
//                Join.JoinInfo joinInfo = new Join.JoinInfo(j_name, j_id, j_pw);
//
//                sendObjectData(socket,JOIN,joinInfo);
//                System.out.println("2\n");
//
//                receiveData(socket);
//                if(C_isOK == 1){
//                    System.out.println("회원가입이 완료되었습니다.");
//                    C_isOK = 0; //변수 초기화
//                    loginPage(socket);
//                 }
//            }
//        }
//
//
//
//    }

    public void loadMainMenu(Socket socket) throws IOException{
        int inputNum;

        System.out.println("\nClient :: loadMainMenu()"); //FOR_DEBUG

        //숫자 말고 다른게 들어올 때 처리
        while(true){
            System.out.println("1. 영화 예매");
            System.out.println("2. 영화 추천");
            System.out.println("3. 예매 확인");
            System.out.println("4. 로그아웃");

            while(true) {
                inputNum = sc.nextInt();
                if(inputNum<0 || inputNum>4){
                    System.out.println("잘못 입력하셨습니다. 다시 입력해주세요");
                }
                else{
                    break;
                }
            }

            if(inputNum==1) {
                MovieReservationClient movieReservationClient = new MovieReservationClient();

                sendData(socket, RESERVATION, 5);

                byte[] nameObjectData = receiveObjectData(socket);
                MovieReservationObject.MovieName movieName = toObject(nameObjectData, MovieReservationObject.MovieName.class);
                int MovieId = movieReservationClient.MovieReservationMovieName(this, movieName);

                sendData(socket, RESERVATION, MovieId); //선택한 영화 보내기

                byte[] dateObjectData = receiveObjectData(socket);
                MovieReservationObject.MovieDate movieDate = toObject(dateObjectData, MovieReservationObject.MovieDate.class);
                String inputMovieDate = movieReservationClient.MovieReservationDate(this, movieDate); //선택한 날짜
                MovieReservationObject.InputMovieDate inputMovieDateObject = new MovieReservationObject.InputMovieDate(
                        inputMovieDate);
                sendObjectData(socket, RESERVATION, inputMovieDateObject); //선택한 날짜 보내기

                byte[] timeObjectData = receiveObjectData(socket);
                MovieReservationObject.MovieTime movieTime = toObject(timeObjectData, MovieReservationObject.MovieTime.class);
                String inputMovieTime = movieReservationClient.MovieReservationTime(this, movieTime); //선택한 시간
                MovieReservationObject.InputMovieTime inputMovieTimeObject = new MovieReservationObject.InputMovieTime(
                        inputMovieTime);
                sendObjectData(socket, RESERVATION, inputMovieTimeObject); //선택한 날짜 보내기

                byte[] seatObjectData = receiveObjectData(socket);
                MovieReservationObject.MovieSeat movieSeat = toObject(seatObjectData, MovieReservationObject.MovieSeat.class);
                ArrayList inputmovieSeat = movieReservationClient.MovieReservationSeat(this, movieSeat);
                MovieReservationObject.MovieSeatNum inputMovieSeatObject = new MovieReservationObject.MovieSeatNum(
                        inputmovieSeat);
                sendObjectData(socket, RESERVATION, inputMovieSeatObject); //선택한 시트 보내

                byte[] InfoObjectData = receiveObjectData(socket);
                MovieReservationObject.MovieInfo movieInfo = toObject(InfoObjectData, MovieReservationObject.MovieInfo.class);
                int InfoCheck = movieReservationClient.MovieReservationInfo(this, movieInfo);
                sendData(socket, RESERVATION, InfoCheck);
            }
            else if(inputNum==2){
                GenreSearchClient genreSearchClient = new GenreSearchClient();
                sendData(socket,GENRE,GENRE);

                byte[] genreListObjectData=receiveObjectData(socket);
                GenreSearchObject.GenreList genreList=toObject(genreListObjectData, GenreList.class);
                String genreNumber=genreSearchClient.GenreSearchList(this, genreList);
                GenreSearchObject.GenreName genreNumObject = new GenreSearchObject.GenreName(genreNumber);
                sendObjectData(socket, GENRE, genreNumObject); //선택한 시트 보내

                byte[] movieListObjectData=receiveObjectData(socket);
                GenreSearchObject.GenreMovieName movieList=toObject(movieListObjectData, GenreMovieName.class);
                genreSearchClient.MovieNamePrint(this, movieList);
            }
            else if(inputNum==3){
                sendData(socket,MYINFO,MYINFO);

                byte[] MovidInfoObjectData = receiveObjectData(socket);
                MyPageObject.MyPageInfo movieInfoObject = toObject(MovidInfoObjectData, MyPageInfo.class);
                MypageClient_02.MyPageClinet(this, movieInfoObject); //선택한 날짜
            }
            else if(inputNum==4){
                sendData(socket, 9,9);
                break;
            }
        }
    }


    //send Object
    public static void sendObjectData(Socket socket, int menuNum,Object obj) throws IOException {
        System.out.println("\nClient :: sendObjectData() ::");   //FOR DEBUG

        C_dataType=1;
        C_isData=1;


        //생성한 객체를 byte array로 변환
        byte[] objectData = toByteArray(obj);   //앞에 2byte엔 헤더 붙여야됨
        //System.out.printf("Object data size: 0x%x\n",objectData.length); // 객체 사이즈 출력
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
    public static byte[] receiveObjectData(Socket socket) throws IOException {
        System.out.println("\nClient :: receiveObject() ::");   //FOR DEBUG
        //수신 버퍼의 최대 사이즈 지정
        int maxBufferSize = 2048; //1024
        //버퍼 생성
        byte[] recvBuffer = new byte[maxBufferSize];
        //서버로부터 받기 위한 입력 스트림 뚫음
        InputStream is = socket.getInputStream();
        //버퍼(recvBuffer) 인자로 넣어서 받음. 반환 값은 받아온 size
        int nReadSize = is.read(recvBuffer);
        System.out.printf("@@nReadSize: 0x%x\n",nReadSize);
        System.out.println("+++++++RecvData 내부 +++++++");
//        for(int i = 0; i < nReadSize; i++)
//        {
//            System.out.printf("recvB[%d]: 0x%x\n",i, recvBuffer[i]);
//        }
        // 헤더 추출
        int header = ((recvBuffer[0] & 0xFF) << 8) | (recvBuffer[1] & 0xFF);
        System.out.printf("buffer[0]: 0x%x, buffer[1]: 0x%x\n", recvBuffer[0],recvBuffer[1]);
        //System.out.printf("Header: 0x%x\n", header);
        parseData_de(2,header);
        // 나머지 데이터 복사
        byte[] objectData = new byte[nReadSize - 2];
        System.arraycopy(recvBuffer, 2, objectData, 0, nReadSize - 2);

        System.out.printf("##objectData: 0x%x\n",objectData.length);
//        for(int i = 0; i < objectData.length; i++)
//        {
//            System.out.printf("objD[%d]: 0x%x\n",i, objectData[i]);
//        }
        //받아온 값이 0보다 클때 = 받은 값이 존재할 때
        if (nReadSize > 0) {

//            if(C_menuNum==5){ //가현
//                MovieReservationObject.MovieName movieName  = toObject(objectData, MovieReservationObject.MovieName.class);
//                for (Pair<Integer, String> integerStringPair : movieName.movieNumArray) {
//                    System.out.println("dd" + integerStringPair);
//                }
//            }
            return objectData;
        }
        return null;
    }


    ////ToObject()/////////////////
    public static <T> T toObject (byte[] bytes, Class<T> type)
    {
        System.out.println("Client :: toObject() ::");   //FOR DEBUG
        Object obj = null;
        System.out.println("@@@@@@@");
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            System.out.println("Received object type: " + obj.getClass().getName());
            System.out.println("!!!!!!!!");
        }
        catch (IOException ex) {
            //TODO: Handle the exception
            System.out.println("$$$$$$$$");
            System.out.println("IOException: " + ex.getMessage());
            ex.printStackTrace(); // 이 부분을 추가하여 오류의 상세 내용을 출력
        }
        catch (ClassNotFoundException ex) {
            //TODO: Handle the exception
            System.out.println("&&&&&&&");
        }

        return type.cast(obj);
    }

    //send Byte
    public static void sendData(Socket socket,int menuNum,int data)throws IOException{
        System.out.println("\nClient :: sendData() ::");   //FOR DEBUG

        OutputStream os = socket.getOutputStream();

        int header=0;

        C_dataType = 0;
        if(data != 0)
            C_isData = 1;
        else
            C_isData = 0;

        header = parseData_en(menuNum) << 16;

        data |= header;

        System.out.printf("Senddata: 0x%x\n",data);
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
        System.out.println("\nSever:: receiveData() ::");
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
        System.out.println("\nClient :: parseData_de() ::");
        //dataType = 1 => 헤더, 데이터 모두 전달
        //dataType = 2 => 헤더 값만 전달
        short header = 0;
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

        short header = 0;
        //TEST
        //Login menu : 0x440;

        C_networkType = 0;
        C_menuNum = menuNum;
        header = (short)(((C_networkType << 15) & 0x8000)|((C_isError << 14) & 0x6000)|
                ((C_errorCode << 11) & 0x3800)|((C_isData << 10) & 0x600)|
                ((C_dataType << 9) & 0x200)|((C_menuNum << 6) & 0x1C0)|
                ((C_idNum << 1) & 0x3E)|(C_isOK  & 0x1));
        System.out.printf("\nClient :: parseData_en() :: header: 0x%x\n",header);
        return header;

    }
}
