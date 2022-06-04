package com.example.mytfg.Control;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.example.mytfg.Models.Product;
import com.example.mytfg.R;

import java.util.List;

//Clase del adaptador que hereda de Adapter con un tipo viewHolder que contendra los elementos de la vista
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> implements View.OnClickListener{
    List<Product> productList;
    private View.OnClickListener clickListener;

    //Constructor del adaptador, al que le indicamos que usara una lista de objetos
    public RecyclerAdapter(List<Product> productList){
        this.productList = productList;
    }

    @NonNull
    @Override
    //Metodo que crea la estructura de los componentes de cada celda
    //Con LayoutInflater cogemos la vista de la celda y la anidamos a la estructura jerárquica del padre
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_model,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);
        view.setOnClickListener(this);
        return recyclerHolder;

    }

    @Override
    //Asignamos la informacion a cada elemento de nuestra vista
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        Product product = productList.get(position);
        holder.txtFoodProduct.setText(product.getProduct());
        holder.txtFoodPrice.setText(product.getPrice()+" ");
        holder.txtFoodNumber.setText(" "+product.getNumber()+".");
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    //Indicamos que listener usara el metodo onClickListener
    public void setOnClickListener(View.OnClickListener listener){
        this.clickListener = listener;
    }

    //Sobrescribimos los metodos onClickListener y onLongClickListener
    @Override
    public void onClick(View view) {
        if(clickListener != null) {
            clickListener.onClick(view);
        }
    }

    public class RecyclerHolder extends ViewHolder{
        TextView txtFoodProduct;
        TextView txtFoodPrice;
        TextView txtFoodNumber;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            txtFoodProduct = (TextView)  itemView.findViewById(R.id.txtFoodProduct);
            txtFoodPrice = (TextView)  itemView.findViewById(R.id.txtFoodPrice);
            txtFoodNumber = itemView.findViewById(R.id.txtFoodNumber);
        }
    }
}
