package xdml;

public class MyService {
    @Log(logLevel = "ERROR")
    public void queryDatabase(int i) {
        System.out.println("query db");
    }

    @Log
    public void provideHttpResponse(String abc) {
        System.out.println("provide http service");
    }

    public void noLog() {
        System.out.println("I have no Log!");
    }
}
