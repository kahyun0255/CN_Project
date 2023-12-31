package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.example.*;
import org.example.GenreSearchObject.GenreList;
import org.example.GenreSearchObject.GenreMovieName;
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
    public static final int MYINFO = 7;
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
        ArrayList ClientUsers=new ArrayList<>();

//        System.out.println("\tClient :: main()"); //FOR_DEBUG
        String hostname = "localhost";
        int port = 7778;

        try {
            Socket socket=new Socket(hostname, port);
//            System.out.println("\tConnected to Server");

            Client clinet = new Client(socket);

            int name;
            while(true){
                name=(int)(Math.random()*100000);
                if(!ClientUsers.contains(name)){
                    break;
                }
            }


            Thread sendThread = new SenderThread(socket,name);
            sendThread.start();

            int loginCheck = Login.loginPage(socket);
            if(loginCheck == 1)
                clinet.loadMainMenu(socket);

        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }


    public void loadMainMenu(Socket socket) throws IOException{
        int inputNum = 0;

//        System.out.println("\tClient :: loadMainMenu()"); //FOR_DEBUG

        while(true){
            System.out.println(" ");
            System.out.println("1. 영화 예매");
            System.out.println("2. 장르 검색");
            System.out.println("3. 예매 확인");
            System.out.println("4. 로그아웃");
            System.out.print("입력하세요: ");
            while(true) {
                try {
                    inputNum = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("숫자를 입력해야 합니다. 다시 시도해주세요.");
                    sc.next(); // 입력 버퍼 초기화
                }
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
                if(InfoCheck == 1)
                    C_isOK = 1;
                else
                    C_isOK = 0;
                sendData(socket, RESERVATION, InfoCheck);
                
                int reservationCheck = receiveData(socket);
                if(reservationCheck ==0){
                    System.out.println("예매가 취소되었습니다. 다시 시도해주세요");
                }
                else if(reservationCheck==1){
                    System.out.println("예매가 완료되었습니다.");
                }
                else if(reservationCheck==3){
                    System.out.println("다른 사용자가 이미 선택한 좌석입니다.");

                }
                C_isOK = 0;
            }
            else if(inputNum==2){
                GenreSearchClient genreSearchClient = new GenreSearchClient();
                sendData(socket,GENRE,GENRE);

                byte[] genreListObjectData=receiveObjectData(socket);
                GenreSearchObject.GenreList genreList=toObject(genreListObjectData, GenreList.class);
                String genreNumber=genreSearchClient.GenreSearchList(this, genreList);
                GenreSearchObject.GenreName genreNumObject = new GenreSearchObject.GenreName(genreNumber);
                sendObjectData(socket, GENRE, genreNumObject);

                byte[] movieListObjectData=receiveObjectData(socket);
                GenreSearchObject.GenreMovieName movieList=toObject(movieListObjectData, GenreMovieName.class);
                genreSearchClient.MovieNamePrint(this, movieList);
            }
            else if(inputNum==3){
                sendData(socket,MYINFO,MYINFO);

                byte[] MovidInfoObjectData = receiveObjectData(socket);
                MyPageObject.MyPageInfo movieInfoObject = toObject(MovidInfoObjectData, MyPageInfo.class);
                MypageClient_02.MyPageClinet(this, movieInfoObject);
            }
            else if(inputNum==4){
                sendData(socket, 9,9);
                break;
            }
        }
    }


    //send Object
    public static void sendObjectData(Socket socket, int menuNum,Object obj) throws IOException {
//        System.out.println("\tClient :: sendObjectData() ::");   //FOR DEBUG

        C_dataType=1;
        C_isData=1;

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
//        System.out.printf("\tdataWithHeader data size: 0x%x\n",dataWithHeader.length); // 객체 사이즈 출력

        //서버로 내보내기 위한 출력 스트림 뚫음
        OutputStream os = socket.getOutputStream();
        //출력 스트림에 데이터 씀
        //os.write(objectData);
        os.write(dataWithHeader);
        //보냄
        os.flush();
        C_isOK = 0;
    }

    ////////ToByteArray////////////
    public static byte[] toByteArray (Object obj)
    {
        //System.out.println("Client :: toByteArray() ::");   //FOR DEBUG
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
//        System.out.println("\tClient :: receiveObject() ::");   //FOR DEBUG
        //수신 버퍼의 최대 사이즈 지정
        int maxBufferSize = 2048; //1024
        //버퍼 생성
        byte[] recvBuffer = new byte[maxBufferSize];
        //서버로부터 받기 위한 입력 스트림 뚫음
        InputStream is = socket.getInputStream();
        //버퍼(recvBuffer) 인자로 넣어서 받음. 반환 값은 받아온 size
        int nReadSize = is.read(recvBuffer);
        //System.out.printf("@@nReadSize: 0x%x\n",nReadSize);
        //System.out.println("+++++++RecvData 내부 +++++++");
//        for(int i = 0; i < nReadSize; i++)
//        {
//            System.out.printf("recvB[%d]: 0x%x\n",i, recvBuffer[i]);
//        }
        // 헤더 추출
        int header = ((recvBuffer[0] & 0xFF) << 8) | (recvBuffer[1] & 0xFF);
        //System.out.printf("buffer[0]: 0x%x, buffer[1]: 0x%x\n", recvBuffer[0],recvBuffer[1]);
        //System.out.printf("Header: 0x%x\n", header);
        parseData_de(2,header);
        // 나머지 데이터 복사
        byte[] objectData = new byte[nReadSize - 2];
        System.arraycopy(recvBuffer, 2, objectData, 0, nReadSize - 2);

        //System.out.printf("##objectData: 0x%x\n",objectData.length);
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
        //System.out.println("Client :: toObject() ::");   //FOR DEBUG
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream (bis);
            obj = ois.readObject();
            //System.out.println("Received object type: " + obj.getClass().getName());
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
//        System.out.println("\tClient :: sendData() ::");   //FOR DEBUG

        OutputStream os = socket.getOutputStream();

        int header=0;

        C_dataType = 0;
        if(data != 0)
            C_isData = 1;
        else
            C_isData = 0;

        header = parseData_en(menuNum) << 16;

        data |= header;

//        System.out.printf("\tSendData: 0x%x\n",data);
        // 4바이트 크기의 버퍼를 생성합니다.
        byte[] buffer = new byte[4];

        buffer[0] = (byte) (data >> 24);
        buffer[1] = (byte) (data >> 16);
        buffer[2] = (byte) (data >> 8);
        buffer[3] = (byte) (data);

        // 바이트 배열을 스트림을 통해 전송합니다.
        os.write(buffer);
        os.flush(); // 버퍼에 남아있는 데이터를 모두 전송합니다.
        C_isOK = 0;
    }
    public static int receiveData(Socket socket) throws IOException{
//        System.out.println("\tClient:: receiveData() ::");
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

//        System.out.printf("\tReceived integer: 0x%x\n",value);

        int recvData = parseData_de(1,value);
        //System.out.printf("recvData: 0x%x\n",recvData);
        return recvData;

    }

    static int parseData_de(int DataType, int value)
    {
//        System.out.printf("\tClient :: parseData_de() ::");
        //dataType = 1 => 헤더, 데이터 모두 전달
        //dataType = 2 => 헤더 값만 전달
        short header = 0;
        int data = 0;

        if(DataType == 1) {
            header = (short)(value >> 16) ;
            data = value & 0xFF;
        }
        else if(DataType == 2){
            header = (short)value;
        }
//        System.out.printf(" h: 0x%x, d: 0x%x\n",header, data);

        C_networkType = (header >> 15) & 0x01;
        C_isError = (header >> 14) & 0x01;
        C_errorCode = (header >> 11) & 0x07;
        C_isData = (header >> 10) & 0x01;
        C_dataType = (header >> 9) & 0x01;
        C_menuNum = (header >> 6) & 0x07;
        C_idNum = (header >> 1)& 0x1F;
        C_isOK = header & 0x1;

//        System.out.printf("\tnt: %x, iE: %x, eC: %x, iD: %x, dT: %x, mN: %x, iN: %x, iO:%x\n"
//                ,C_networkType,C_isError,C_errorCode,C_isData
//                ,C_dataType,C_menuNum,C_idNum,C_isOK);
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
//        System.out.printf("\tClient :: parseData_en() :: header: 0x%x\n",header);
        return header;

    }
}

class SenderThread extends Thread{
    Socket socket=null;
    int name;

    Scanner sc=new Scanner(System.in);

    public SenderThread(Socket socket, int name){
        this.socket=socket;
        this.name=name;
    }
    public void run(){
        try{
            PrintStream out=new PrintStream(socket.getOutputStream());
            out.println(name);
            out.flush();

//            while(true){
//                    String outputMsg = sc.nextLine();
//                    out.println(outputMsg);
//                    out.flush();
//                    if("quit".equals(outputMsg)) break;
//            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
