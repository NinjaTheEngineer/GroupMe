package ninja.engineer.GroupMeProjetoFinal;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.sa90.materialarcmenu.ArcMenu;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;

    private ViewPagerAdapter viewPagerAdapter;

    private String groupObjectId;
    private String userName;

    FeedFrag feedFrag;
    ChatFrag chatFrag;
    InfoFrag infoFrag;
    Bundle b, b2, b3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = ApplicationClass.user.getProperty("name").toString();
        //id do grupo que escolhemos
        groupObjectId = getIntent().getStringExtra("extraCurrentGroup");


        b = new Bundle();
        b.putString("groupId", groupObjectId);
        b.putString("userName", userName);

        feedFrag = new FeedFrag();
        feedFrag.setArguments(b);

        b2 = new Bundle();
        b2.putString("channelName", ApplicationClass.userCurrentChannel);
        b2.putString("userName", userName);

        chatFrag = new ChatFrag();
        chatFrag.setArguments(b2);

        b3 = new Bundle();

        infoFrag = new InfoFrag();

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("groupMe");
        getSupportActionBar().setElevation(0);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myViewPager = (ViewPager) findViewById(R.id.viewPager_id);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Adicionar Fragmentos aqui
        viewPagerAdapter.AddFragment(chatFrag, "Chat");
        viewPagerAdapter.AddFragment(feedFrag, "Feed");
        viewPagerAdapter.AddFragment(infoFrag, "Info");


        myViewPager.setAdapter(viewPagerAdapter);
        myTabLayout.setupWithViewPager(myViewPager);

        myTabLayout.getTabAt(0).setIcon(R.drawable.ic_comment_btn);
        myTabLayout.getTabAt(1).setIcon(R.drawable.ic_android);
        myTabLayout.getTabAt(2).setIcon(R.drawable.ic_group);


        myTabLayout.getTabAt(1).select();
    }

    //Menu de opções
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

        //Função do Menu de opções
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_option){
            chatFrag.disconnectChannel();
            Backendless.UserService.logout(new AsyncCallback<Void>() {
                @Override
                public void handleResponse(Void response) {
                    //User logged out
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    //
                }
            });
            startActivity(new Intent(MainActivity.this, Login.class));
        }
        if(item.getItemId() == R.id.main_choose_group_option){
            Intent intent = new Intent(MainActivity.this, ChooseGroup.class);
            intent.putExtra("extraUserObjectId", getIntent().getStringExtra("userObjectId"));

            chatFrag.disconnectChannel();

            startActivity(intent);
        }
        if(item.getItemId() == R.id.main_new_group_option){
            Intent intent = new Intent(MainActivity.this, FirstGroup.class);

            chatFrag.disconnectChannel();

            startActivity(intent);
            }
        MainActivity.this.finish();
        return true;
    }
}