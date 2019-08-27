package com.nurgulmantarci.realmdatabaseapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.nurgulmantarci.realmdatabaseapp.adapter.CustomAdapter;
import com.nurgulmantarci.realmdatabaseapp.interfaces.IClickListener;
import com.nurgulmantarci.realmdatabaseapp.model.PersonTable;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements IClickListener {

    private Realm realm;
    private RecyclerView recyclerView;
    private EditText etName, etSurname, etAge, etDepartment;
    private Button btnAddPerson, btnDismiss;
    private CustomAdapter customAdapter;
    private List<PersonTable> personList = new ArrayList<>();
    private int pos;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();
        Init();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        customAdapter = new CustomAdapter(context, personList, MainActivity.this);
        recyclerView.setAdapter(customAdapter);

        refreshList();

    }


    private void Init() {
        etName = findViewById(R.id.personNameEdit);
        etSurname = findViewById(R.id.personSurnameEdit);
        etDepartment = findViewById(R.id.personDepartmentEdit);
        etAge = findViewById(R.id.personAgeEdit);
        btnAddPerson = findViewById(R.id.addBttn);
        recyclerView = findViewById(R.id.recycler_view);
        btnDismiss = findViewById(R.id.dismissBttn);
    }

    public void btnClickAddPerson(View view) {
        if(btnAddPerson.getText().toString().equalsIgnoreCase("KAYDET")){


            if(checkFields()){

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {

                        Number maxId = bgRealm.where(PersonTable.class).max("id");
                        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                        PersonTable personTable = bgRealm.createObject(PersonTable.class,nextId);
                        personTable.setName(etName.getText().toString());
                        personTable.setSurname(etSurname.getText().toString());
                        personTable.setDepartment(etDepartment.getText().toString());
                        personTable.setAge(Integer.parseInt(etAge.getText().toString()));

                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Kayıt başarılı bir şekilde eklendi.", Toast.LENGTH_SHORT).show();
                        refreshList();
                        clearAllFields();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }else{
                Toast.makeText(MainActivity.this, "Gerekli alanları giriniz!", Toast.LENGTH_SHORT).show();
            }


        }else{

            if(checkFields()){

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {

                        RealmResults<PersonTable> realmResults = Realm.getDefaultInstance().where(PersonTable.class).findAll();
                        final PersonTable updateTable = realmResults.get(pos);
                        updateTable.setName(etName.getText().toString());
                        updateTable.setSurname(etSurname.getText().toString());
                        updateTable.setDepartment(etDepartment.getText().toString());
                        updateTable.setAge(Integer.parseInt(etAge.getText().toString()));

                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Kayıt başarılı bir şekilde güncellendi.", Toast.LENGTH_SHORT).show();
                        refreshList();
                        clearAllFields();
                        btnAddPerson.setText("Kaydet");
                        btnDismiss.setVisibility(View.GONE);
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }


    private void clearAllFields(){
        etName.setText("");
        etSurname.setText("");
        etDepartment.setText("");
        etAge.setText("");
    }

    private void refreshList(){
       RealmResults<PersonTable> realmResults=realm.where(PersonTable.class).findAll();
       personList.clear();
       for(PersonTable person: realmResults){
           personList.add(person);
       }
       customAdapter.notifyDataSetChanged();
    }

    private boolean checkFields(){
        if(etName.getText().toString().length()>0 && etSurname.getText().toString().length()>0 && etDepartment.getText().toString().length()>0 && etAge.getText().toString().length()> 0){
            return true;
        }
        return false;
    }

    private void fillAllFields(String _name, String _surname, String _department, String _age ){
        etName.setText(_name);
        etSurname.setText(_surname);
        etDepartment.setText(_department);
        etAge.setText(_age);
    }

    @Override
    public void onMenuclick(View view,final int position) {
        final PopupMenu popupMenu=new PopupMenu(context,view);
        popupMenu.inflate(R.menu.menu_item);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.editPersonItem:
                        updatePerson(position);
                        break;
                    case R.id.deletePersonItem:
                        deletePerson(position);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void updatePerson(int _position){
        RealmResults<PersonTable> realmResults=realm.where(PersonTable.class).findAll();
        final PersonTable updatePerson=realmResults.get(_position);
        fillAllFields(updatePerson.getName(),updatePerson.getSurname(),updatePerson.getDepartment(),String.valueOf(updatePerson.getAge()));
        btnAddPerson.setText("Güncelle");
        btnDismiss.setVisibility(View.VISIBLE);
        this.pos=_position;
    }

    private void deletePerson(final int _position){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PersonTable deletePerson=personList.get(_position);
                deletePerson.deleteFromRealm();
                refreshList();
            }
        });
    }


    public void btnClickDissmisChange(View view) {
        clearAllFields();
        btnAddPerson.setText("Kaydet");
        btnDismiss.setVisibility(View.GONE);
    }


}
