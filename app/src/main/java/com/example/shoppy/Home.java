package com.example.shoppy;

import android.content.Intent;
import android.os.Bundle;

import com.example.shoppy.Interface.ItemClickListener;
import com.example.shoppy.Model.Category;
import com.example.shoppy.Service.ListenOrder;
import com.example.shoppy.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG="Logging Example:";

    private AppBarConfiguration mAppBarConfiguration;
    ActionBarDrawerToggle mdrawerToggle;
    DrawerLayout drawer;
    private boolean homeShouldOpenDrawer;
    private boolean mToolBarNavigationListenerIsRegistered = false;


    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //initNavigationDrawer();


        //init paper
        Paper.init(this);

        drawer = findViewById(R.id.drawer_layout);
         NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               int id=menuItem.getItemId();
                   if(id==R.id.nav_menu){
                       Intent homeIntent=new Intent(Home.this,Home.class);
                       startActivity(homeIntent);
                       return true;

        }
        else if(id==R.id.nav_cart){
            Intent cartIntent=new Intent(Home.this,Cart.class);
            startActivity(cartIntent);
            return true;
        }
        else if(id==R.id.nav_orders){
            Intent orderIntent=new Intent(Home.this,OrderStatus.class);
            startActivity(orderIntent);
            return true;
        }
        else if(id==R.id.nav_log_out){
            //remove id & passsword from paper ,LOGOUT
             Paper.book().destroy();


            Intent signIn=new Intent(Home.this,signin.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(signIn);
                       return true;
        }

                DrawerLayout drawerLayout=(DrawerLayout)findViewById(R.id.nav_view);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        mdrawerToggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mdrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);

            }
        });


        drawer.addDrawerListener(mdrawerToggle);
        mdrawerToggle.syncState();
        //Init Firebase

        database=FirebaseDatabase.getInstance();
        category=database.getReference("category");


        //setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent cartIntent=new Intent(Home.this,Cart.class);
               startActivity(cartIntent);
            }
        });



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_menu, R.id.nav_cart, R.id.nav_orders,
                R.id.nav_log_out)
                .setDrawerLayout(drawer)
                .build();
       // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
       // NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
       // NavigationUI.setupWithNavController(navigationView, navController);



        //set name for user
        View headerView=navigationView.getHeaderView(0);
        txtFullName=(TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(common.currentUser.getName());

        //Load menu
        recycler_menu=(RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);

        if(common.isConnectedToInternet(this))
            loadMenu();
        else {
            Toast.makeText(this, "Please check your internet connection!!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Register services
        Intent service=new Intent(Home.this, ListenOrder.class);
        startService(service);

    }





    private void loadMenu() {

        FirebaseRecyclerOptions<Category> options;



        options=new FirebaseRecyclerOptions.Builder<Category>().setQuery(category,Category.class).build();
        adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder menuViewHolder, int i, @NonNull Category category) {


               Picasso.get().load(category.getImage()).into(menuViewHolder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });

                menuViewHolder.txtMenuName.setText(category.getName());
                final Category clickItem=category;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Get categoryId and send to new activity
                        Intent foodList=new Intent(Home.this,FoodList.class);

                        //Because CategoryId is the key ,get key of this item
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);

                    }
                });

            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false);
                return new MenuViewHolder(view);
            }
        };
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getApplicationContext(),1);
        recycler_menu.setLayoutManager(gridLayoutManager);
        adapter.startListening();
        recycler_menu.setAdapter(adapter);
    // Log.e(TAG,"loadmenu()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

   /*@Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.refresh)
            loadMenu();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id=menuItem.getItemId();


        switch (menuItem.getItemId())
        {
            case R.id.nav_home:
                Intent homeIntent=new Intent(Home.this,Home.class);
                startActivity(homeIntent);
                return true;
            case R.id.nav_cart:
                Intent cartIntent=new Intent(Home.this,Cart.class);
                startActivity(cartIntent);
                return true;
            case R.id.nav_orders:
                Intent orderIntent=new Intent(Home.this,OrderStatus.class);
                startActivity(orderIntent);
                return true;
            case R.id.nav_log_out:
                //remove id & passsword from paper ,LOGOUT
                Paper.book().destroy();


                Intent signIn=new Intent(Home.this,signin.class);
                signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signIn);
                return true;

        }



        DrawerLayout drawerLayout=(DrawerLayout)findViewById(R.id.nav_view);
        drawerLayout.closeDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(menuItem);


    }
}
