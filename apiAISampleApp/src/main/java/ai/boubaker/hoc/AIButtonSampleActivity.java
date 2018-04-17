package ai.boubaker.hoc;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.google.gson.Gson;

import org.json.JSONException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import ai.boubaker.hoc.Models.*;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ai.api.android.AIConfiguration;
import ai.api.android.GsonFactory;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import ai.api.sample.R;
import ai.api.ui.AIButton;

public class AIButtonSampleActivity extends BaseActivity implements AIButton.AIButtonListener, TextToSpeech.OnInitListener {

    public static final String TAG = AIButtonSampleActivity.class.getName();
    List<String> Responses = new ArrayList<>();
    List<String> img_floor = new ArrayList<>();
    private AIButton aiButton;
    String urll="";
    ImageView fotoView;
    private ListView mList;
    List<Users> HoC = new ArrayList<>();
    String URL1;
    List<IALink> HoC1 = new ArrayList<>();
    DateFormat format = new SimpleDateFormat("dd MMMM yyyy");
    int indice = -1;
    Users HoC2 = null;
    Users a;
    boolean signed_in = false;
    boolean exist = false;
    private Gson gson = GsonFactory.getGson();
    int i=2;
    TextView html;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String uri = "https://aiassistantserver.azurewebsites.net/api/";
    List<String> commands = new ArrayList<String>();
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aibutton_sample);
        getSupportActionBar().hide();
        pref = getApplicationContext().getSharedPreferences("Identif", MODE_PRIVATE);
        editor = pref.edit();
        html = new TextView(this);
        commands.add("");
        commands.add("");
        aiButton = (AIButton) findViewById(R.id.micButton);
        mList = (ListView) findViewById(R.id.listView1);

        final AIConfiguration config = new AIConfiguration(Config.ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        config.setRecognizerStartSound(getResources().openRawResourceFd(R.raw.test_start));
        config.setRecognizerStopSound(getResources().openRawResourceFd(R.raw.test_stop));
        config.setRecognizerCancelSound(getResources().openRawResourceFd(R.raw.test_cancel));
        aiButton.initialize(config);
        aiButton.setResultsListener(this);
        Toast.makeText(AIButtonSampleActivity.this, "Say sign in to begin", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
            aiButton.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        aiButton.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_aibutton_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(AISettingsActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResult(final AIResponse response) {
        runOnUiThread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @SuppressLint("LongLogTag")
            @Override
            public void run() {
                final Result result = response.getResult();
//                Log.e("Intent intreged",result.getMetadata().getIntentName());
                if ((signed_in == true)&&(result.getMetadata().getIntentName().contains("Sign in"))){
                    result.getFulfillment().setSpeech("You are already signed in.");
                    commands.add(result.getFulfillment().getSpeech());
                    TTS.speak(result.getFulfillment().getSpeech());
                    i++;
                }
                if ((signed_in == true)&&(result.getMetadata().getIntentName().contains("Sign out"))){
                    commands.add(result.getFulfillment().getSpeech());
                    TTS.speak(result.getFulfillment().getSpeech());
                    i++;
                    signed_in = false;
                    show(result);
                    return;
                }
                if ((signed_in == false)&&(result.getMetadata().getIntentName().contains("Sign out"))){
                    result.getFulfillment().setSpeech("You are already signed out.");
                    commands.add(result.getFulfillment().getSpeech());
                    TTS.speak(result.getFulfillment().getSpeech());
                    i++;
                    show(result);
                    return;
                }
                if (commands.get(i-1).contains("Ok, please give me")){
                    commands.add(result.getFulfillment().getSpeech());
                    i++;
                    int pageNumber = pref.getInt("key_name2", -1);
                    int abc = -1;
                    if(pageNumber == -1){
                        AsyncTask<String, Void, String> s1 = new Fetch.HttpAsyncTask().execute(uri+"AiLinks");
                            try {
                                HoC1 = string_json_visitor(s1.get());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        abc = HoC1.get(HoC1.size()-1).getId_usr();
                        editor.putInt("key_name2", abc);
                        editor.commit();
                    }
                    else{
                        abc = pageNumber;
                    }

                    AsyncTask<String, Void, String> s = new Fetch.HttpAsyncTask().execute(uri+"Visitors/"+abc);
                    try {
                        HoC2 = string_json_viss(s.get());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    switch(commands.get(i-2)) {
                        case "Ok, please give me your full date of birth.":
                            String formattedDate = parse_from_db(HoC2.getUser_dob().toString());
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("1st","1"));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("2nd","2"));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("3rd","3"));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("th",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("of",""));
                            Log.e("Compare", formattedDate.toUpperCase()+" <> "+ result.getResolvedQuery().toUpperCase());
                            if(formattedDate.toUpperCase().equals(result.getResolvedQuery().toUpperCase())){
                                    indice = 0;
                                    exist = true;
                                }
                            break;

                        case "Ok, please give me your phone number.":
                            Log.e("Compare", HoC2.getUser_phone().toString()+" <> "+ result.getResolvedQuery().replaceAll("\\s",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("-",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll(" ",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("e",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("th",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("/",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("[()]",""));
                            if(HoC2.getUser_phone().toString().equals(result.getResolvedQuery().replaceAll("\\s",""))){
                                indice = 0;
                                exist = true;
                            }
                            break;

                        case "Ok, please give me your pin number.":
                            Log.e("Compare", HoC2.getPin().toString()+" <> "+ result.getResolvedQuery().replaceAll("\\s",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll(" ",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("/",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("[()]",""));
                            result.setResolvedQuery(result.getResolvedQuery().toUpperCase().replaceAll("\\s",""));
                            if(HoC2.getPin().toUpperCase().toString().equals(result.getResolvedQuery())){
                                indice = 0;
                                exist = true;
                            }
                            break;

                        case "Ok, please give me your address postal code.":
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("-",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("th",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll(" ",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("[()]",""));
                            result.setResolvedQuery(result.getResolvedQuery().replaceAll("/",""));
                            HoC2.setUser_postal(HoC2.getUser_postal().toString().toUpperCase().replaceAll("\\s+", ""));
                            Log.e("Compare", HoC2.getUser_postal().toString()+" <> "+ result.getResolvedQuery());
                            if( HoC2.getUser_postal().toUpperCase().equals(result.getResolvedQuery().toUpperCase())){
                                    indice = 0;
                                    exist = true;
                            }
                            break;

                    }
                    if ((exist == true)&&(signed_in==false)){
                        result.getFulfillment().setSpeech("Welcome "+HoC2.getUser_name()+", you are signed in");
                        TTS.speak(result.getFulfillment().getSpeech());
                        signed_in = true;
                        exist = false;
                    }
                    else if (exist == false){
                        result.getFulfillment().setSpeech("We are sorry we can't sign you in");
                        TTS.speak(result.getFulfillment().getSpeech());
                    }
                    commands.add(result.getFulfillment().getSpeech());
                    i++;
                    show(result);
                    return;
                }
                if ((signed_in == true)||(result.getMetadata().getIntentName().equals("Sign in"))
                        ||(result.getAction().contains("smalltalk"))){
                    if (result.getAction().contains("smalltalk")){
                        TTS.speak(result.getFulfillment().getSpeech());
                    }
                    if (result.getMetadata().getIntentName().equals("Sign in"))
                        TTS.speak(result.getFulfillment().getSpeech());

                    if (result.getResolvedQuery().contains("time")){
                        Calendar rightNow = Calendar.getInstance();
                        String  currentTime = "Now it's "+rightNow.get(Calendar.HOUR)+":"+rightNow.get(Calendar.MINUTE);
                        result.getFulfillment().setSpeech(currentTime.toString());
                        TTS.speak(result.getFulfillment().getSpeech());
                    }
                    if (result.getMetadata().getIntentName().contains("member.bus")){
                        result.getFulfillment().setSpeech("Please wait.");
                        TTS.speak(result.getFulfillment().getSpeech());
                        Calendar sCalendar = Calendar.getInstance();
                        int hour = sCalendar.getTime().getHours();
                        int minute = sCalendar.getTime().getMinutes();
                        int minute_new = (((int)(minute/10))+1)*10;
                        if(minute_new-minute==1){
                            result.getFulfillment().setSpeech("Next bus in "+(minute_new-minute)+" minute.");
                            TTS.speak(result.getFulfillment().getSpeech());
//                            if (minute_new == 60){
//                                minute_new = 0;
//                                hour = hour++;
//                            }
                            result.getFulfillment().setSpeech("Next bus in "+(minute_new-minute)+" minute.");
                        }
                        else {
                            result.getFulfillment().setSpeech("Next bus in "+(minute_new-minute)+" minutes.");
                            TTS.speak(result.getFulfillment().getSpeech());
//                            if (minute_new == 60){
//                                minute_new = 00;
//                                hour = hour++;
//                            }
                            result.getFulfillment().setSpeech("Next bus in "+(minute_new-minute)+" minute.");
                        }
                    }
                    if (result.getMetadata().getIntentName().contains("member.now")){
                        result.getFulfillment().setSpeech("Please wait.");
                        TTS.speak(result.getFulfillment().getSpeech());
                        AsyncTask<String, Void, String> s = null;
                        String data="";
//                        try{
//                            s = new Fetch.HttpAsyncTask().execute("http://www.ourcommons.ca");
//                        }
//                        catch (Exception e){
//                        }
//                        try {
//                            data = s.get();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        }
//                        Log.e("data content", data);
//                        Pattern pattern = Pattern.compile(".*<span class=\"now-in-the-house-subtitle\">(.*?)</span >.*");
//                        Matcher matcher = pattern.matcher(data);
//                        if (matcher.find()) {
//                            result.getFulfillment().setSpeech(matcher.group(1));
//                            TTS.speak(result.getFulfillment().getSpeech());
//                        }
//                        else Toast.makeText(AIButtonSampleActivity.this, "Nothing to show", Toast.LENGTH_LONG).show();
//
                        List <String> msgs = new ArrayList<>();
                        msgs.add("The House is currently sitting. Current Member Speaking: The Honourable Pierre Poilievre Conservative.");
                        msgs.add("The House is currently sitting. Current Member Speaking: The Speaker Geoff Regan Liberal.");
                        msgs.add("The House is currently sitting. Current Member Speaking: Carol Hughes NDP.");
                        msgs.add("The House is currently sitting. Current Member Speaking: Mark Gerretsen Liberal.");
                        int rand = ThreadLocalRandom.current().nextInt(0, 4);
                        result.getFulfillment().setSpeech(msgs.get(rand));
                        TTS.speak(result.getFulfillment().getSpeech());
                    }
                    if (result.getMetadata().getIntentName().contains("member.menu")){
                        String dayLongName = result.getFulfillment().getSpeech();
                        if (dayLongName.length()==0){
                            result.getFulfillment().setSpeech("The data base is empty");
                            TTS.speak(result.getFulfillment().getSpeech());
                            commands.add(result.getFulfillment().getSpeech());
                            i++;
                            show(result);
                            return;
                        }
                        result.getFulfillment().setSpeech("Please wait.");
                        TTS.speak(result.getFulfillment().getSpeech());
                        Calendar sCalendar = Calendar.getInstance();
                        if (dayLongName.toUpperCase().equals("TODAY")){
                            dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                        }
                        if (dayLongName.toUpperCase().equals("TOMORROW")){
                            sCalendar.add(Calendar.DATE, 1);
                            dayLongName = sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                            Log.e("Day", dayLongName);
                        }
                        if ((dayLongName.toUpperCase().equals("SATURDAY"))||(dayLongName.toUpperCase().equals("SUNDAY"))) {
                            result.getFulfillment().setSpeech("The cafeteria is closed for the weekend.");
                            TTS.speak(result.getFulfillment().getSpeech());
                            commands.add(result.getFulfillment().getSpeech());
                            i++;
                            show(result);
                            return;
                        }
                        Log.e("Link", uri+"Foods/"+dayLongName);
                        AsyncTask<String, Void, String> s = null;
                        try{
                            s = new Fetch.HttpAsyncTask().execute(uri+"Foods/"+dayLongName);
                        }
                        catch (Exception e){
                        }
                        food HoC_m = null;
                        try {
                            HoC_m = string_meet_food(s.get());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        if (HoC_m == null){
                            result.getFulfillment().setSpeech("The data base is empty");
                            TTS.speak(result.getFulfillment().getSpeech());
                        }
                        else{
                            result.getFulfillment().setSpeech(dayLongName+" we have "+HoC_m.getDescription());
                            TTS.speak(result.getFulfillment().getSpeech());
                        }
                    }
                    if (result.getMetadata().getIntentName().contains("member.expense")){
                        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                        Date date = new Date();
                        String s="You have 2 expense claims:\n";
                        s = s+"1. Expense claim for in riding mileage submitted the "+dateFormat.format(date)+", status processed amount of 324 credited to your account\n" +
                                "2. Expense claim for trip to Winnipeg from Ottawa status pending explanation, do you want an officer to call you about this one.";
                        result.getFulfillment().setSpeech(s);
                        TTS.speak(result.getFulfillment().getSpeech());
                    }
                    if (result.getMetadata().getIntentName().contains("visitor.meet.")){
                        result.getFulfillment().setSpeech("Please wait.");
                        TTS.speak(result.getFulfillment().getSpeech());
                        AsyncTask<String, Void, String> s = null;
                        try{
                            s = new Fetch.HttpAsyncTask().execute(uri+"Meetings"+"/"+HoC2.getId());
                        }
                        catch (Exception e){
                        }
                        List<Meetings> HoC_m = new ArrayList<>();
                        try {
                            HoC_m = string_json_m(s.get());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        int min_ind = sort(HoC_m);
                        if (HoC_m.size()==0){
                            result.getFulfillment().setSpeech("The data base is empty.");
                            TTS.speak(result.getFulfillment().getSpeech());
                        }
                        else{

                            if(result.getMetadata().getIntentName().contains("visitor.meet.room")){
                                String sp = "This is the plan of the floor to get to the meeting's room.\n";
                                URL1 = HoC_m.get(min_ind).getRoom().getImg();
                                fotoView = new ImageView(AIButtonSampleActivity.this);
                                Log.e("Link", URL1);
                                fotoView.setTag(URL1);
                                new DownloadImagesTask().execute(fotoView);
                                result.getFulfillment().setSpeech(sp);
                                TTS.speak(result.getFulfillment().getSpeech());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                fotoView.setLayoutParams(layoutParams);
                                new AlertDialog.Builder(AIButtonSampleActivity.this)
                                        .setTitle("Help:")
                                        .setView(fotoView)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(R.drawable.iconn)
                                        .show();
                            }

                            if(result.getMetadata().getIntentName().contains("visitor.meet.last")){
                                String sp="";
                                try{
                                    sp= "Your next meeting is on "+HoC_m.get(min_ind).getDate()+" with "+HoC_m.get(min_ind).getChair().getName()
                                        +", "+HoC_m.get(min_ind).getRoom().getName()+" in "+HoC_m.get(min_ind).getRoom().getBuilding()+", floor number "+HoC_m.get(min_ind).
                                        getRoom().getFloor()+" and it's about "+HoC_m.get(min_ind).getDescription();}
                                catch (Exception e){
                                    sp = "The database is empty.";
                                }
                                result.getFulfillment().setSpeech(sp);
                                TTS.speak(result.getFulfillment().getSpeech());
                            }
                            if(result.getMetadata().getIntentName().contains("visitor.meet.held")){
                                String sp = "Your next meeting is in "+HoC_m.get(min_ind).getRoom().getName();
                                result.getFulfillment().setSpeech(sp);
                                TTS.speak(result.getFulfillment().getSpeech());
                            }

                            if(result.getMetadata().getIntentName().contains("visitor.meet.when")){
                                String sp = "Your next meeting is on "+HoC_m.get(min_ind).getDate()+" about "+HoC_m.get(min_ind).getDescription();
                                result.getFulfillment().setSpeech(sp);
                                TTS.speak(result.getFulfillment().getSpeech());
                            }

                            if(result.getMetadata().getIntentName().contains("visitor.meet.who")){
                                String sp = "The next meeting will be chaired by  "+HoC_m.get(min_ind).getChair().getName();
                                result.getFulfillment().setSpeech(sp);
                                TTS.speak(result.getFulfillment().getSpeech());
                            }
                            if(result.getMetadata().getIntentName().contains("visitor.meet.more")){
                                urll = HoC_m.get(min_ind).getUri();
                                String sp = "For more information press here.";
                                Log.e("Link for meeting", ""+urll+" <> "+HoC_m.get(min_ind).getId()+" <> "+HoC_m.get(min_ind).getDescription()+min_ind);
                                result.getFulfillment().setSpeech(sp);
                                TTS.speak(result.getFulfillment().getSpeech());
                            }
                        }
                        commands.add(result.getFulfillment().getSpeech());
                        i++;
                    }

                    if (result.getMetadata().getIntentName().equals("tech.getSwitch")){
                        String s1 = "OFFICE";
                        String x = result.getResolvedQuery().toUpperCase();
                        x = x.substring(x.indexOf(s1)+s1.length()+1);
                        String ind =  "";
                        ind = x.replaceAll("\\s+", "");
                        result.getFulfillment().setSpeech("Please wait.");
                        TTS.speak(result.getFulfillment().getSpeech());
                        AsyncTask<String, Void, String> s = null;
                        try{
                            s = new Fetch.HttpAsyncTask().execute(uri+"Offices/GetOfficeByIdName/"+ind);
                        }
                        catch (Exception e){
                            Log.e("error", ""+e);
                        }
                        List<Offices> HoC_offices = new ArrayList<>();
                        try {
                            HoC_offices = string_json_Offices(s.get());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        if (HoC_offices.size()==0){
                            result.getFulfillment().setSpeech("The switch does not exist or the database is empty.");
                            TTS.speak(result.getFulfillment().getSpeech());
                        }
                        else{
                            String sp="";
                            try{
                                sp = "The office "+HoC_offices.get(0).getDescription()+ " is served by switch "+HoC_offices.get(0).getSwitcher().getName()+".";
                                result.getFulfillment().setSpeech(sp);
                                TTS.speak(result.getFulfillment().getSpeech());}
                            catch(Exception e){
                                result.getFulfillment().setSpeech("The data base is empty");
                                TTS.speak(result.getFulfillment().getSpeech());
                            }
                        }
                        commands.add(result.getFulfillment().getSpeech());
                        i++;
                    }

                    if (result.getMetadata().getIntentName().equals("tech.getOffice")){
                        String s1 = "SWITCH";
                        String x = result.getResolvedQuery().toUpperCase();
                        try {
                            x = x.substring(x.indexOf(s1)+s1.length()+1);
                        }catch (Exception e){
                            Log.e("Error", ""+e);
                        }
                        String ind;
                        ind = x.toUpperCase().replaceFirst("\\s+", "");
                        ind = ind.toUpperCase().replaceAll(":", "");
                        try {
                            ind = ind.substring(0, ind.indexOf(' '));
                        }
                        catch (Exception e){
                        }
                        result.getFulfillment().setSpeech("Please wait.");
                        TTS.speak(result.getFulfillment().getSpeech());
                        AsyncTask<String, Void, String> s = null;
                        try{
                            s = new Fetch.HttpAsyncTask().execute(uri+"Offices/GetMOfficeBySwName/"+ind);
                        }
                        catch (Exception e){
                        }
                        List<Offices> HoC_sw = new ArrayList<>();
                        try {
                            HoC_sw = string_json_sw(s.get());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        if (HoC_sw.size()==0){
                            result.getFulfillment().setSpeech("The switch does not exist or the database is empty.");
                            TTS.speak(result.getFulfillment().getSpeech());
                        }
                        else{
                            String sp = "Switch "+ind+ " serves offices: ";
                            for (int j=0; j<HoC_sw.size(); j++){
                                if (j<HoC_sw.size()-1)
                                    sp = sp+HoC_sw.get(j).getDescription()+",\n";
                                else
                                    sp = sp+HoC_sw.get(j).getDescription()+".";
                            }
                            result.getFulfillment().setSpeech(sp);
                            TTS.speak(result.getFulfillment().getSpeech());
                        }
                        commands.add(result.getFulfillment().getSpeech());
                        i++;
                    }
                    if (result.getMetadata().getIntentName().equals("inter.tech")){
                        String ind;
                        try{
                            ind = result.getFulfillment().getSpeech();}
                        catch (Exception e){
                            Toast.makeText(AIButtonSampleActivity.this, "Tell me again", Toast.LENGTH_LONG).show();
                            return;
                        }
                        result.getFulfillment().setSpeech("Please wait.");
                        TTS.speak(result.getFulfillment().getSpeech());
                        AsyncTask<String, Void, String> s = null;
                        try{
                            s = new Fetch.HttpAsyncTask().execute(uri+"Interventions/GetInterventionByTechnition/"+ind);
                        }
                        catch (Exception e){
                        }
                        List<Interventions> HoC_sw = new ArrayList<>();
                        try {
                            HoC_sw = string_json_inter_sw(s.get());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        if (HoC_sw.size()==0){
                            result.getFulfillment().setSpeech("The data base is empty.");
                            TTS.speak(result.getFulfillment().getSpeech());
                        }
                        else{
                            String sp = "Techninican "+ind+" has performed the following:\n";
                            for (int j=0; j<HoC_sw.size(); j++){
                                sp = sp+(j+1)+". On "+parse_from_db(HoC_sw.get(j).getDate())+", "+HoC_sw.get(j).getDescription()+" on switch "+
                                        HoC_sw.get(j).getswitcher().getName()+".\n";
                            }
                            result.getFulfillment().setSpeech(sp);
                            TTS.speak(result.getFulfillment().getSpeech());
                        }
                        commands.add(result.getFulfillment().getSpeech());
                        i++;
                    }

                    if (result.getMetadata().getIntentName().equals("inter.sw")){
                        String ind ="";
                        String sear ="SWITCH";
                        ind = result.getResolvedQuery().toUpperCase();
                        ind = ind.substring(ind.indexOf(sear)+sear.length()+1);
                        ind = ind.replaceFirst("\\s","");
                        ind = ind.replaceFirst(":","");
                        if (ind.indexOf(" ")!=-1)
                            ind = ind.substring(0, ind.indexOf(" "));
                        result.getFulfillment().setSpeech("Please wait.");
                        TTS.speak(result.getFulfillment().getSpeech());
                        AsyncTask<String, Void, String> s = null;
                        try{
                            s = new Fetch.HttpAsyncTask().execute(uri+"Interventions/GetInterventionBySwName/"+ind);
                        }
                        catch (Exception e){
                        }
                        List<Interventions> HoC_sw = new ArrayList<>();
                        try {
                            HoC_sw = string_json_inter_sw(s.get());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        if (HoC_sw.size()==0){
                            result.getFulfillment().setSpeech("The data base is empty.");
                            TTS.speak(result.getFulfillment().getSpeech());
                        }
                        else{
                            int min_ind = sort_interv(HoC_sw);
                            String sp = "Switch "+ind+" serviced by "+HoC_sw.get(min_ind).gettechnician().getFull_name()+", his id is "+
                                    HoC_sw.get(min_ind).gettechnician().getId()+" on "+HoC_sw.get(min_ind).getDate()+" for "+HoC_sw.
                                    get(min_ind).getDescription()+".";
                            result.getFulfillment().setSpeech(sp);
                            TTS.speak(result.getFulfillment().getSpeech());
                        }
                        commands.add(result.getFulfillment().getSpeech());
                        i++;
                    }
                    commands.add(result.getFulfillment().getSpeech());
                    i++;
                    show(result);
            }
                else {
                    result.getFulfillment().setSpeech("I'm sorry i can't help you until you sign in.");
                    show(result);
                    TTS.speak(result.getFulfillment().getSpeech());
                }
            }

        });
    }

    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AIButtonSampleActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCancelled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onCancelled");
            }
        });
    }

    private void startActivity(Class<?> cls) {
        final Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
    public String parser(Date x){
        SimpleDateFormat spf = new SimpleDateFormat("dd MMMM yyyy");
        String newDateString = spf.format(x);
        return newDateString;
    }
    public String parse_from_db(String x){
        String d = x.substring(0, x.indexOf("T"));
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat targetFormat = new SimpleDateFormat("d MMMM yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = targetFormat.format(date);
        return  formattedDate;
    }
    public List<Meetings> string_json_m(String res) throws JSONException
    {
        List<Meetings> Hoc = new ArrayList<>();
        Meetings[] jsonArr = new Gson().fromJson(res, Meetings[].class);
        if (jsonArr != null) {
            for (Meetings m : jsonArr) {
                Meetings a = new Meetings();
                a.setId(m.getId());
                a.setName(m.getName());
                a.setDate(m.getDate());
                a.setRoom(m.getRoom());
                a.setChair(m.getChair());
                a.setDescription(m.getDescription());
                a.setUri(m.getUri());
                Hoc.add(a);
            }
        }
        return Hoc;
    }
    public List<Interventions> string_json_inter_sw(String res) throws JSONException
    {
        List<Interventions> Hoc = new ArrayList<>();
        Interventions[] jsonArr = new Gson().fromJson(res, Interventions[].class);
        if (jsonArr != null) {
            for (Interventions inter : jsonArr){
                Interventions a = new Interventions();
                a.setId(inter.getId());
                a.setDate(inter.getDate());
                a.setDescription(inter.getDescription());
                a.settechnician(inter.gettechnician());
                a.setswitcher(inter.getswitcher());
                Hoc.add(a);
            }
        }
        return Hoc;
    }
    public List<IALink> string_json_visitor(String res) throws JSONException
    {
        List<IALink> Hoc = new ArrayList<>();
        IALink[] jsonArr = new Gson().fromJson(res, IALink[].class);
        if (jsonArr != null) {
            for (IALink inter : jsonArr){
                IALink a = new IALink();
                a.setId(inter.getId());
                a.setId_usr(inter.getId_usr());
                a.setType(inter.getType());
                Hoc.add(a);
            }
        }
        return Hoc;
    }
    public List<Offices> string_json_Offices(String res) throws JSONException
    {
        List<Offices> Hoc = new ArrayList<>();
        Offices jsonArr = new Gson().fromJson(res, Offices.class);
        Offices a = new Offices();
        if (jsonArr != null) {
            a.setId(jsonArr.getId());
            a.setName(jsonArr.getName());
            a.setDescription(jsonArr.getDescription());
            a.setSwitcher(jsonArr.getSwitcher());
        }
        Hoc.add(a);
        return Hoc;
    }
    public List<Offices> string_json_sw(String res) throws JSONException
    {
        List<Offices> Hoc = new ArrayList<>();
        Offices[] jsonArr = new Gson().fromJson(res, Offices[].class);
        if (jsonArr != null) {
            for (Offices off : jsonArr) {
                Offices a = new Offices();
                a.setId(off.getId());
                a.setName(off.getName());
                a.setDescription(off.getDescription());
                Hoc.add(a);
            }
        }
        return Hoc;
    }
    public List<Users> string_json(String res) throws JSONException
    {
        List<Users> Hoc = new ArrayList<>();
        Users[] jsonArr = new Gson().fromJson(res, Users[].class);
        if (jsonArr != null) {
            for (Users us : jsonArr) {
                Users a = new Users();
                a.setId(us.getId());
                a.setUser_name(us.getUser_name());
                a.setUser_phone(us.getUser_phone());
                a.setUser_postal(us.getUser_postal());
                a.setUser_dob(us.getUser_dob());
                Hoc.add(a);
            }
        }
        return Hoc;
    }
    public Users string_json_viss(String res) throws JSONException
    {
        Users jsonArr = new Gson().fromJson(res, Users.class);
        Users a = new Users();
        if (jsonArr != null) {
            a.setId(jsonArr.getId());
            a.setUser_name(jsonArr.getUser_name());
            a.setUser_phone(jsonArr.getUser_phone());
            a.setUser_postal(jsonArr.getUser_postal());
            a.setUser_dob(jsonArr.getUser_dob());
            a.setPin(jsonArr.getPin());
        }
        return a;
    }
    public food string_meet_food(String res) throws JSONException
    {
        food jsonArr = new Gson().fromJson(res, food.class);
        food a = new food();
        if ((jsonArr != null)) {
            a.setId(jsonArr.getId());
            a.setId(jsonArr.getId());
            a.setDay(jsonArr.getDay());
            a.setDescription(jsonArr.getDescription());
        }
        return a;
    }

    @Override
    public void onInit(int status) {

    }

    public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

        ImageView imageView = null;

        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            return download_Image((String)imageView.getTag());
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

        private Bitmap download_Image(String url) {

            Bitmap bmp =null;
            try{
                URL ulrn = new URL(url);
                HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                if (null != bmp)
                    return bmp;

            }catch(Exception e){}
            return bmp;
        }
    }
    public int sort (List<Meetings> abc) {
        int k=-1;
        DateFormat format = new SimpleDateFormat("d MMMM yyyy");
        final long now = System.currentTimeMillis();
        List<Date> dates = new ArrayList<Date>();
        for(int a=0; a<abc.size(); a++){
            abc.get(a).setDate(parse_from_db(abc.get(a).getDate()));
        }
        for (int j = 0; j < abc.size(); j++) {
            try {
                dates.add(format.parse(abc.get(j).getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Date closest = Collections.min(dates, new Comparator<Date>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public int compare(Date d1, Date d2) {
                long diff1 = Math.abs(d1.getTime() - now);
                long diff2 = Math.abs(d2.getTime() - now);
                return Long.compare(diff1, diff2);
            }
        });
        for (int j = 0; j < abc.size(); j++) {
            if (dates.get(j) == closest){
                k = j;
            }
        }
        return k;
    }
    public int sort_interv(List<Interventions> abc) {
        int k=-1;
        DateFormat format = new SimpleDateFormat("d MMMM yyyy");
        final long now = System.currentTimeMillis();
        List<Date> dates = new ArrayList<Date>();
        for(int a=0; a<abc.size(); a++){
            abc.get(a).setDate(parse_from_db(abc.get(a).getDate()));
        }
        for (int j = 0; j < abc.size(); j++) {
            try {
                dates.add(format.parse(abc.get(j).getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Date closest = Collections.min(dates, new Comparator<Date>() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public int compare(Date d1, Date d2) {
                long diff1 = Math.abs(d1.getTime() - now);
                long diff2 = Math.abs(d2.getTime() - now);
                return Long.compare(diff1, diff2);
            }
        });
        for (int j = 0; j < abc.size(); j++) {
            if (dates.get(j) == closest){
                k = j;
            }
        }
        return k;
    }

    public void show(Result result){
        Responses.add("User: "+result.getResolvedQuery()+"\n"+"AI: "+result.getFulfillment().getSpeech());
        if(result.getMetadata().getIntentName().contains("visitor.meet.room"))
            img_floor.add(URL1);
        else
            img_floor.add("");

        CustomListAdapter test = new CustomListAdapter(AIButtonSampleActivity.this, Responses.toArray(new String[Responses.size()]),
                img_floor.toArray(new String[img_floor.size()]), result.getMetadata().getIntentName());
        if (mList.getCount()<5){
            mList.setTranscriptMode(0);
            mList.setStackFromBottom(false);
        }
        else{
            mList.setTranscriptMode(2);
            mList.setStackFromBottom(true);
        }
        mList.setAdapter(test);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ((urll != null)&&((Responses.get(position).contains("For more information press here.")))) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urll));
                    startActivity(intent);
                }
            }
        });

    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            moveTaskToBack(true);
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {@Override public void run() {doubleBackToExitPressedOnce=false;}}, 2000);
    }
}