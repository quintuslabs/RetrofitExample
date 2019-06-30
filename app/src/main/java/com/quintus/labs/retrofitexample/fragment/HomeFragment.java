package com.quintus.labs.retrofitexample.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.quintus.labs.retrofitexample.R;
import com.quintus.labs.retrofitexample.model.User;
import com.quintus.labs.retrofitexample.util.Constant;
import com.quintus.labs.retrofitexample.util.LocalStorage;
import com.squareup.picasso.Picasso;


/**
 * RetrofitExample
 * https://github.com/quintuslabs/RetrofitExample
 * Created on 18-Feb-2019.
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */
public class HomeFragment extends Fragment {
    String userString, name, email, mobile, gender, dob, address, api_key, image;
    TextView nameTv, emailTv, mobileTv, genderTv, dobTv, addressTv;
    ImageView imageView;
    LocalStorage localStorage;
    Gson gson;
    User user;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        localStorage = new LocalStorage(getContext());
        gson = new Gson();
        userString = localStorage.getUserLogin();
        user = gson.fromJson(userString, User.class);
        name = user.getName();
        email = user.getEmail();
        mobile = user.getMobile();
        gender = user.getGender();
        dob = user.getDob();
        address = user.getAddress();
        image = user.getImage();

        nameTv = view.findViewById(R.id.name_tv);
        emailTv = view.findViewById(R.id.email_tv);
        mobileTv = view.findViewById(R.id.mobile_tv);
        genderTv = view.findViewById(R.id.gender_tv);
        dobTv = view.findViewById(R.id.dob_tv);
        addressTv = view.findViewById(R.id.address_tv);
        imageView = view.findViewById(R.id.image_view);
        api_key = user.getApi_key();

        nameTv.setText(name);
        emailTv.setText(email);
        mobileTv.setText(mobile);
        genderTv.setText(gender);
        dobTv.setText(dob);
        addressTv.setText(address);

        String imagePath = Constant.IMAGE_PATH + image;
        Log.d("Image Path ", imagePath);
        if (image != null) {
            Picasso.get().load(imagePath).error(R.drawable.no_image).into(imageView);
        } else {
            Picasso.get().load(R.drawable.no_image).into(imageView);

        }


        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Home");
    }

}
