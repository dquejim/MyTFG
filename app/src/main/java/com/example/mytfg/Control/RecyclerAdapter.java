package com.example.mytfg.Control;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.example.mytfg.Models.Food;
import com.example.mytfg.R;
import java.util.List;

//Clase del adaptador que hereda de Adapter con un tipo viewHolder que contendra los elementos de la vista
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> implements View.OnClickListener{
    List<Food>  foodList;
    private int lastPosition = -1;
    private View.OnClickListener clickListener;

    //Constructor del adaptador, al que le indicamos que usara una lista de objetos
    public RecyclerAdapter(List<Food> foodList){
            this.foodList = foodList;
    }

    @NonNull
    @Override
    //Metodo que crea la estructura de los componentes de cada celda
    //Con LayoutInflater cogemos la vista de la celda y la anidamos a la estructurajerárquica del padre
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_model,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);
        return recyclerHolder;

    }

    @Override
    //Asignamos la informacion a cada elemento de nuestra vista
    public void onBindViewHolder(@NonNull RecyclerHolder holder,int position) {
        Food food = foodList.get(position);
        holder.txtFoodProduct.setText("   "+food.getProduct());
        holder.txtFoodPrice.setText(food.getPrice());

        //Animacion al cargar los elementos de la vista
        setAnimation(holder.itemView,position);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    @Override
    public void onClick(View view) {
        if(clickListener != null) {
            clickListener.onClick(view);
        }
    }

    //Indicamos que listener usara el metodo onClickListener
    public void setOnClickListener(View.OnClickListener listener){
        this.clickListener = listener;
    }

    public class RecyclerHolder extends ViewHolder{
        TextView txtFoodProduct;
        TextView txtFoodPrice;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            txtFoodProduct = (TextView)  itemView.findViewById(R.id.txtFoodProduct);
            txtFoodPrice = (TextView)  itemView.findViewById(R.id.txtFoodPrice);
        }
    }

    //Animacion de carga del reciclerView
    private void setAnimation(View view,int position){
        if(position > lastPosition) {
            AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(500);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }


}
