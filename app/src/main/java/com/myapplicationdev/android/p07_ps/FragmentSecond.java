package com.myapplicationdev.android.p07_ps;


import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecond extends Fragment {

    EditText etWord;
    Button btnRetrieveWord, btnEmail;
    TextView tvWord;
    String content = "";
    private Cursor cursor;
    public FragmentSecond() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        etWord = view.findViewById(R.id.etContainWord);
        btnRetrieveWord = view.findViewById(R.id.btnRetrieveWord);
        tvWord = view.findViewById(R.id.tvWordSMS);
        btnEmail = view.findViewById(R.id.btnEmail);

        btnRetrieveWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etWord.getText().toString();
                if (!TextUtils.isEmpty(data)) {
                    Uri uri = Uri.parse("content://sms");
                    String[] reqCols = new String[]{"date", "address", "body", "type"};
                    String filter = "body LIKE ?";
                    String[] arg = {"%" + data + "%"};
                    ContentResolver cr = getActivity().getContentResolver();
                    cursor = cr.query(uri, reqCols, filter, arg, null);
                    if (data.contains(" ")){
                        String[] dataArr = data.split(" ");
                        String[] args = new String[dataArr.length];
                        args[0] = "%" + dataArr[0] + "%";
                        for (int i = 1; i<dataArr.length; i++){
                            filter += "AND body LIKE ?";
                            args[i] = "%"+dataArr[i]+"%";
                        }
                        cursor = cr.query(uri, reqCols, filter, args, null);
                    }
                    String smsBody = "";
                    if (cursor.moveToFirst()) {
                        do {
                            long dateInMillis = cursor.getLong(0);
                            String date = (String) DateFormat.format("dd MM yyyy h:mm:ss aa", dateInMillis);
                            String address = cursor.getString(1);
                            String body = cursor.getString(2);
                            String type = cursor.getString(3);
                            if (type.equalsIgnoreCase("1")) {
                                type = "Inbox: ";
                            } else {
                                type = "Sent: ";
                            }
                            smsBody += type + " " + address + "\n at " + date
                                    + "\n\"" + body + "\"\n\n";
                        } while (cursor.moveToNext());
                    }
                    content = smsBody;
                    tvWord.setText(smsBody);
                    if (smsBody.isEmpty()) {
                        Toast.makeText(getActivity(), "No Results Found", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Please Enter A Word", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The action you want this intent to do;
                // ACTION_SEND is used to indicate sending text
                Intent email = new Intent(Intent.ACTION_SEND);
                // Put essentials like email address, subject & body text
                email.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"dljh1234@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT,
                        "SMS Content");
                email.putExtra(Intent.EXTRA_TEXT, content);
                // This MIME type indicates email
                email.setType("message/rfc822");
                // createChooser shows user a list of app that can handle
                // this MIME type, which is, email
                startActivity(Intent.createChooser(email,
                        "Choose an Email client :"));

            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnRetrieveWord.performClick();
                } else {
                    Toast.makeText(getContext(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
