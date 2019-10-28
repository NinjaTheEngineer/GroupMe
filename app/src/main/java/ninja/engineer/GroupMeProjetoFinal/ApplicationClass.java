package ninja.engineer.GroupMeProjetoFinal;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.ArrayList;
import java.util.List;

public class ApplicationClass extends Application {

    public static final String APPLICATION_ID = "1E88526A-9D87-617B-FF8C-AFCE4E5FE200";
    public static final String API_KEY = "0A8D8AA1-7A68-D106-FF1D-1AE0D785C600";
    public static final String SERVER_URL = "https://api.backendless.com";
    public static final String DEFAULT_CHANNEL = "chat";
    public static String userCurrentChannel;
    public static String userName;
    public static String userGroup;

    public static BackendlessUser user;
    public static List<Group> groups;
    public static ArrayList<Posts> posts;

    @Override
    public void onCreate() {
        super.onCreate();
    //Conecta a aplicação com o servidor Backendless
        Backendless.setUrl( SERVER_URL );
        Backendless.initApp( getApplicationContext(),
                            APPLICATION_ID,
                            API_KEY );
    }
}
