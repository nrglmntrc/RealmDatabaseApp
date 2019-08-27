package com.nurgulmantarci.realmdatabaseapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nurgulmantarci.realmdatabaseapp.R;
import com.nurgulmantarci.realmdatabaseapp.interfaces.IClickListener;
import com.nurgulmantarci.realmdatabaseapp.model.PersonTable;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private List<PersonTable> personList;
    private IClickListener clickListener;

    public CustomAdapter(Context context, List<PersonTable> personList, IClickListener clickListener) {
        this.personList = personList;
        this.clickListener = clickListener;
        this.context=context;

    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        public TextView name,surname,department, age, personOptionMenu;

        public MyViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.personName);
            surname=itemView.findViewById(R.id.personSurname);
            department=itemView.findViewById(R.id.personDepartment);
            age=itemView.findViewById(R.id.personAge);
            personOptionMenu=itemView.findViewById(R.id.textViewOptions);

            personOptionMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onMenuclick(v,getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item,viewGroup,false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, int i) {
        PersonTable person=personList.get(i);
        myViewHolder.name.setText(person.getName());
        myViewHolder.surname.setText(person.getSurname());
        myViewHolder.department.setText(person.getDepartment());
        myViewHolder.age.setText(String.valueOf(person.getAge()));
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }
}
