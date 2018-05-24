package org.projects.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import com.firebase.ui.database.FirebaseAdapter;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class MainActivity extends AppCompatActivity implements MyDialogFragment.OnPositiveListener {

    Map<String, Product> savedCopies = new HashMap<>();


    MyDialogFragment dialog;
    Context context;

    private final int RESULT_CODE_PREFERENCES = 1;

    FirebaseListAdapter<Product> adapter;
    ListView listView;
    //ArrayList<Product> bag = new ArrayList<>();

    public FirebaseListAdapter<Product> getMyAdapter()
    {
        return adapter;
    }

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference firebase = mRootRef.child("items");


    //This method is the one we need to implement from the
    //interface. It will be called when the user has clicked the
    //positive button (yes button):
    @Override
    public void onPositiveClicked() {
        //Do your update stuff here to the listview
        //and the bag etc
        //just to show how to get arguments from the bag.
        //Toast toast = Toast.makeText(context, "positive button clicked", Toast.LENGTH_LONG);
        //toast.show();
        //bag.clear(); //here you can do stuff with the bag and
        //adapter etc.


        //bag.clear();
        mRootRef.setValue(null);
        listView.clearChoices();
        adapter.notifyDataSetChanged();
        Toast.makeText(this,"All items cleared", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Crashlytics.getInstance().crash(); // Force a crash
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Needed to get the toolbar to work on older versions
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean dark = MyPreferenceFragment.isDark(this);
        String name = MyPreferenceFragment.getName(this);
        updateUI(name, dark);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("items");


        //do we have some saved state?
//        if (savedInstanceState != null) {
//            System.out.println("test");
//            //ArrayList<Product> savedBag = savedInstanceState.getParcelableArrayList("savedBag");
////            if (savedBag != null) { //did we save something
////                System.out.println("test deeper");
////                bag = savedBag;
////            }
//        }


        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = findViewById(R.id.list);
        //here we create a new adapter linking the bag and the
        //listview
        //adapter =  new ArrayAdapter<Product>(this, android.R.layout.simple_list_item_multiple_choice,bag );

        FirebaseListOptions<Product> options = new FirebaseListOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .setLayout(android.R.layout.simple_list_item_multiple_choice)
                .build();

        adapter = new FirebaseListAdapter<Product>(options) {
            @Override
            protected void populateView(View v, Product product, int position) {

                TextView textView = (TextView) v.findViewById(android.R.id.text1);
                //textView.setTextSize(24); //modify this if you want different size
                textView.setText(product.toString());


            }
        };


        //setting the adapter on the listview
        listView.setAdapter(adapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);




        final EditText addText = findViewById(R.id.addText);
        final EditText addQty = findViewById(R.id.addQty);
        final Spinner addSpinner = findViewById(R.id.addSpinner);

        Button addButton =  findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = (String) addSpinner.getSelectedItem();
                System.out.println("item is: " + item);
                //adapter.add(addText.getText().toString() + " x " + addQty.getText().toString());
                if (addText.length() > 0) {
                   //adapter.add(new Product(addText.getText().toString(), Integer.parseInt(addQty.getText().toString()), item));
                    Product p = new Product(addText.getText().toString(), Integer.parseInt(addQty.getText().toString()), item);
                    firebase.push().setValue(p);
                    getMyAdapter().notifyDataSetChanged();
                }
                //Log.d("Bag","Items in back: "+bag.size());
            }
        });

        //add some stuff to the list so we have something
        // to show on app startup
//        bag.add("Bananas");
//        bag.add("Apples");

    }

    @Override protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }



    //This method is called before our activity is destroyed
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//		/* Here we put code now to save the state */
//        outState.putParcelableArrayList("savedBag", bag);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_share) {
            StringBuilder sb = new StringBuilder();
            Intent intent = new Intent(Intent.ACTION_SEND);

            for (Product bagItem : adapter.getSnapshots()) {
                sb.append(bagItem.toString() + ",");
                sb.append("\n");
            }

            String listToSend = sb.toString();
            intent.putExtra(Intent.EXTRA_TEXT, listToSend);
            intent.setType("text/plain");
            startActivity(intent);
        }

        if (id == R.id.action_settings) {
            //Start our settingsactivity and listen to result - i.e.
            //when it is finished.
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivityForResult(intent,RESULT_CODE_PREFERENCES);
            //notice the 1 here - this is the code we then listen for in the
            //onActivityResult
            return true;
        }
//        if (id == R.id.action_delete && listView.getCheckedItemPosition() > -1) {
//            int position = listView.getCheckedItemPosition();
//            bag.remove(position);
//            adapter.notifyDataSetChanged();
//            listView.setItemChecked(-1,true);
//            return true;
//        }
        if (id == R.id.action_delete) {
            savedCopies.clear();
            SparseBooleanArray positions = listView.getCheckedItemPositions();
            System.out.println(positions);
            for (int i = adapter.getCount() -1; i > -1; i--) {
                //positions.get(i);
                if (positions.get(i)) {
                    saveProductCopy(getMyAdapter().getRef(i).getKey(), adapter.getItem(i));
                    //bag.remove(i);
                    getMyAdapter().getRef(i).setValue(null);
                }
            }

            final View parent = listView;
            Snackbar snackbar = Snackbar
                    .make(parent, "Items Deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            readdDeletedProducts();
                            adapter.notifyDataSetChanged();
                        }
                    });

            if (positions.size() > 0) {
                snackbar.show();
            }

            listView.clearChoices();
            //bag.remove(positions);
            adapter.notifyDataSetChanged();
            //listView.setItemChecked(-1,true);
            //return true;
        }
        if (id == R.id.action_clear) {
            showDialog();

        }

        return super.onOptionsItemSelected(item);
    }



    //This method updates our text views.
    public void updateUI(String name, boolean dark)
    {
        TextView myName = findViewById(R.id.MyName);
        //TextView myGender = findViewById(R.id.myGender);
        myName.setText("Shopping list: " + name );
        if (dark) {
            //myGender.setText(R.string.male);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryCustom));

            LinearLayout mainLayout = findViewById(R.id.entireApp);
            mainLayout.setBackgroundColor(getResources().getColor(R.color.colorBgCustom));
            //setTheme(R.style.AppTheme2);
            //toolbar.getContext().setTheme(R.style.AppTheme2);
        } else {
            //myGender.setText(R.string.female);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            LinearLayout mainLayout = findViewById(R.id.entireApp);
            mainLayout.setBackgroundColor(Color.WHITE);
            //setTheme(R.style.AppTheme);
            //toolbar.getContext().setTheme(R.style.AppTheme);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==RESULT_CODE_PREFERENCES) //the code means we came back from settings
        {
            //I can can these methods like this, because they are static
            boolean dark = MyPreferenceFragment.isDark(this);
            String name = MyPreferenceFragment.getName(this);
            String message = "Dark theme enabled: "+dark;
            Toast toast = Toast.makeText(this,message,Toast.LENGTH_LONG);
            toast.show();
            updateUI(name,dark);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //This is the event handler for the show button
    //This is specified in the xml file that this should
    //be the event handler
    public void showDialog() {
        //showing our dialog.

        dialog = new MyDialog();
        //Here we show the dialog
        //The tag "MyFragement" is not important for us.
        dialog.show(getFragmentManager(), "MyFragment");
    }

    public static class  MyDialog extends MyDialogFragment {


        @Override
        protected void negativeClick() {
            //Here we override the method and can now do something
            Toast toast = Toast.makeText(getActivity(),
                    "negative button clicked", Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    public void saveProductCopy(String position, Product product) {
        savedCopies.put(position, product);
        //readdDeletedProducts();
    }

    public void readdDeletedProducts() {
        for (Map.Entry<String, Product> entry : savedCopies.entrySet()) {
            String key = entry.getKey();
            Product value = entry.getValue();
            //bag.add(key, value);
            firebase.child(key).setValue(value);
        }
        savedCopies.clear();
    }
}
