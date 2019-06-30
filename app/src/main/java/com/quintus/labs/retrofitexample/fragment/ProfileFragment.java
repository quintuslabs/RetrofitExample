package com.quintus.labs.retrofitexample.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.quintus.labs.retrofitexample.R;
import com.quintus.labs.retrofitexample.api.clients.RestClient;
import com.quintus.labs.retrofitexample.model.UploadResponse;
import com.quintus.labs.retrofitexample.model.User;
import com.quintus.labs.retrofitexample.model.UserResult;
import com.quintus.labs.retrofitexample.util.Constant;
import com.quintus.labs.retrofitexample.util.LocalStorage;
import com.quintus.labs.retrofitexample.util.NetworkCheck;
import com.quintus.labs.retrofitexample.util.imageUtil.FileUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * RetrofitExample
 * https://github.com/quintuslabs/RetrofitExample
 * Created on 18-Feb-2019.
 * Created by : Santosh Kumar Dash:- http://santoshdash.epizy.com
 */
public class ProfileFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 20;
    public static String TAG = "ProfileEdit ==>";
    Button back, success, selectImage;
    ImageView profile_image;
    LocalStorage localStorage;
    Uri imageUri;
    RadioButton male, fe_male;
    RadioGroup gender;
    ProgressDialog progressDialog;
    String _name, _email, _mobile, _image, _gender, _dob, _address;
    TextView name, email, mobile, dob;
    EditText address;
    String apiKey, id;
    Intent CamIntent;
    User user;
    private Calendar cal;
    private int day;
    private int month;
    private int year;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);


        back = view.findViewById(R.id.pf_back);
        success = view.findViewById(R.id.pf_success);
        selectImage = view.findViewById(R.id.fpe_select_image);
        profile_image = view.findViewById(R.id.pf_image);
        name = view.findViewById(R.id.pf_name);
        email = view.findViewById(R.id.pf_email);
        mobile = view.findViewById(R.id.pf_mobile);
        dob = view.findViewById(R.id.pf_dob);
        gender = view.findViewById(R.id.pf_gender);
        male = view.findViewById(R.id.pf_male);
        address = view.findViewById(R.id.fp_address);
        fe_male = view.findViewById(R.id.pf_female);

        progressDialog = new ProgressDialog(getContext());


        localStorage = new LocalStorage(getContext());
        Gson gson = new Gson();
        String json = localStorage.getUserLogin();
        User obj = gson.fromJson(json, User.class);
        apiKey = obj.getApi_key();
        _name = obj.getName();
        _email = obj.getEmail();
        _mobile = obj.getMobile();
        _image = obj.getImage();
        _gender = obj.getGender();
        _dob = obj.getDob();
        _address = obj.getAddress();
        id = obj.getId();

        name.setText(_name);
        email.setText(_email);
        mobile.setText(_mobile);
        dob.setText(_dob);
        address.setText(_address);

        if (_gender.equalsIgnoreCase("Male")) {
            male.setChecked(true);
        } else if (_gender.equalsIgnoreCase("FeMale")) {
            fe_male.setChecked(true);
        }

        if (_image != null) {
            Picasso.get().load(Constant.IMAGE_PATH + _image).error(R.drawable.no_image).into(profile_image);
        } else {
            Picasso.get().load(R.drawable.no_image).error(R.drawable.no_image).into(profile_image);
        }


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        } else {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    }
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.CAMERA)) {

                        } else {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    }
                }

                chooseImageDialogBox();
            }
        });


        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        Calendar today = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        dob.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                    }
                };
                Date today = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(today);
                c.add(Calendar.YEAR, -60);
                long minDate = c.getTime().getTime();

                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                //cal.add(Calendar.YEAR, 0);
                long maxDate = cal.getTime().getTime();

                DatePickerDialog dpDialog = new DatePickerDialog(getActivity(), listener, year, month, day);
                dpDialog.getDatePicker().setMinDate(minDate);
                dpDialog.getDatePicker().setMaxDate(maxDate);
                dpDialog.show();
            }
        });

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = view.findViewById(checkedId);
                _gender = radioButton.getText().toString();
            }
        });

        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dob = dob.getText().toString();
                _address = address.getText().toString();
                if (_gender.length() == 0) {
                    Toast.makeText(getContext(), "Please select Your Gender", Toast.LENGTH_LONG).show();
                } else if (_dob.length() == 0) {
                    Toast.makeText(getContext(), "Please select Your Date of Birth", Toast.LENGTH_LONG).show();
                } else if (NetworkCheck.isNetworkAvailable(getContext())) {
                    user = new User();
                    user.setGender(_gender);
                    user.setDob(_dob);
                    user.setAddress(_address);
                    updateUserProfile();
                } else {

                }

                // startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });
        return view;
    }


    private void chooseImageDialogBox() {

        final Dialog imageDialog = new Dialog(getActivity());
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        imageDialog.setContentView(R.layout.choose_image_dialog);
        LinearLayout camera = imageDialog.findViewById(R.id.camera_layout);
        LinearLayout gallery = imageDialog.findViewById(R.id.gallery_layout);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGalleryImage();
                imageDialog.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectCameraImage();
                imageDialog.dismiss();
            }
        });


        imageDialog.show();


    }

    private void selectCameraImage() {

        String fileName = System.currentTimeMillis() + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 0);


    }


    private void selectGalleryImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            Picasso.get().load(imageUri.toString()).fit().into(profile_image);
            if (imageUri != null) {
                uploadFile(imageUri);
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                if (data != null) {
                    imageUri = data.getData();
                    Picasso.get().load(imageUri.toString()).fit().into(profile_image);
                    if (imageUri != null) {
                        uploadFile(imageUri);
                    }

                }
                //onSelectFromGalleryResult(data);
            }
        }
        if (requestCode == 7 && resultCode == Activity.RESULT_OK) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

        }
    }


    private void uploadFile(Uri fileUri) {
        progressDialog.setMessage("Uploading.....");
        progressDialog.show();
        Gson gson = new Gson();
        String json = localStorage.getUserLogin();
        User user = gson.fromJson(json, User.class);
        File file = FileUtils.getFile(getContext(), fileUri);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContext().getContentResolver().getType(fileUri)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        Call<UploadResponse> call = RestClient.getRestService(getContext()).uploadUserImage(user.getApi_key(), body);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                Log.d(TAG, "Response code : " + response.code());
                if (response.code() == 201) {
                    Toast.makeText(getContext(), "image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    User user = response.body().getUser();
                    if (null != user) {
                        Gson gson = new Gson();
                        String userStr = gson.toJson(user);
                        localStorage.setUserLogin(userStr);
                    }
                    progressDialog.dismiss();
                } else {
                    Log.d(TAG, "Response code : " + response.code());
                    Toast.makeText(getContext(), "Please Try again", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Toast.makeText(getContext(), "Please try after sometime", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    private void updateUserProfile() {

        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();
        Log.d(TAG, "update profile!");
        Gson gson = new Gson();
        String userRequesrt = gson.toJson(user);
        Log.d("update string :", userRequesrt);
        Call<UserResult> call = RestClient.getRestService(getContext()).updateUser(apiKey, id, user);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                Log.d(TAG, "Response code : " + response.code());
                if (response.code() == 200) {
                    Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_LONG).show();
                    User user = response.body().getUser();
                    if (null != user) {
                        Gson gson = new Gson();
                        String userStr = gson.toJson(user);
                        Log.d(TAG, userStr);
                        localStorage.setUserLogin(userStr);
                    }
                    progressDialog.dismiss();
                } else {
                    Log.d(TAG, "Response code : " + response.code());
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {

            }
        });

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Profile");
    }

}
