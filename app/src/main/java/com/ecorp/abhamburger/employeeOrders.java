package com.ecorp.abhamburger;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class employeeOrders {

    List<Order> orderList = null;
    Context context;
    LinearLayout layout = null;

    private static employeeOrders instance = null;

    public static employeeOrders getInstance(){
        if(instance == null)
            instance = new employeeOrders();
        return instance;
    }

    public void setAllOrders(View view, Context context){
        Log.e("ORDERS11","HEre1");
        layout = view.findViewById(R.id.employeeOrders).findViewById(R.id.orderList);
        this.context = context;
        if(orderList == null)
            orderList = new ArrayList<Order>();

        setOrders();
    }

    public void setOrders() {


        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Order").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count ", "" + snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Log.e("ORDERS11 ", "" + 1);
                    orderList.add(postSnapshot.getValue(Order.class));
                }
                addOrders();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: ", firebaseError.getMessage());
            }
        });
    }

    public void addOrders(){
        Log.e("ORDERS11", orderList.size()+" :SIZE");
        for(Order order: orderList){
            if(!order.getStatus().equals("Done"))
                addOrder(order);
        }
    }

    public void addOrder(Order order){
        Log.e("ORDERS11","PLUS ONE!");
        View orderView = LayoutInflater.from(context).inflate(R.layout.employee_order, null);
        ((TextView)orderView.findViewById(R.id.fname)).setText("Customer name");
        ((TextView)orderView.findViewById(R.id.orderId)).setText(order.getOrderID());
        if(order.getTime() != null)
            ((TextView)orderView.findViewById(R.id.time)).setText(order.getTime().toString()); //fix
        ((TextView)orderView.findViewById(R.id.notes)).setText(order.getNotes());
        ((EditText)orderView.findViewById(R.id.status)).setText(order.getStatus());
//        setDishesNames(order);
        ((CheckBox)orderView.findViewById(R.id.delivery)).setChecked(order.isDelivery());
        orderView.findViewById(R.id.delivery).setEnabled(false);

        layout.addView(orderView);

    }

    public void setDishesNames(Order order) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        for (int i = 0; i < order.getDishesId().size(); i++) {
            db.child("Dish").child(order.getDishesId().get(i)+"").child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        setDishName(postSnapshot.getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("The read failed: ", firebaseError.getMessage());
                }
            });
        }
    }

    synchronized public void setDishName(String name){
        TextView tv = new TextView(context);
        tv.setText(name);

        ((LinearLayout)layout.findViewById(R.id.orderList)).addView(tv);
    }

}
