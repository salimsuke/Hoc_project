package ai.boubaker.hoc;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import ai.boubaker.hoc.Models.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import ai.api.android.AIConfiguration;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import ai.api.sample.R;
import ai.api.ui.AIButton;

import static android.content.Context.MODE_PRIVATE;

public class AIButtonSampleActivity extends BaseActivity implements AIButton.AIButtonListener, TextToSpeech.OnInitListener {

    public static final String TAG = AIButtonSampleActivity.class.getName();
    List<String> Responses = new ArrayList<>();
    List<String> img_floor = new ArrayList<>();
    private AIButton aiButton;
    String urll="";
    private static final String[] formats = {
            "yyyy-MM-dd'T'HH:mm:ss'Z'",   "yyyy-MM-dd'T'HH:mm:ssZ", "dd MMMM yyyy",
            "yyyy-MM-dd'T'HH:mm:ss",      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss",        "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'",
            "MM/dd/yyyy'T'HH:mm:ss.SSSZ", "MM/dd/yyyy'T'HH:mm:ss.SSS",
            "MM/dd/yyyy'T'HH:mm:ssZ",     "MM/dd/yyyy'T'HH:mm:ss",
            "yyyy:MM:dd HH:mm:ss",        "yyyyMMdd", "yyyy-MM-dd", "HH:mm:ss", "HH:mm" };
    ImageView fotoView;
    private ListView mList;
    String URL1;
    List<IALink> HoC1 = new ArrayList<>();
    int indice = -1;
    Users HoC2 = null;
    SharedPreferences language;
    Users a;
    TextView loc = null;
    boolean signed_in = false;
    boolean exist = false;
    JSONObject data_weather  = null;
    int i=2;
    TextView html;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    AIConfiguration config;
    String uri = "https://aiassistantserver.azurewebsites.net/api/";
    List<String> commands = new ArrayList<String>();
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aibutton_sample);
        getSupportActionBar().hide();

        // initialize TTS
        TTS.init(getApplicationContext());

        pref =      getApplicationContext().getSharedPreferences("Identif", MODE_PRIVATE);
        language =  getApplicationContext().getSharedPreferences("lang", MODE_PRIVATE);
        editor = pref.edit();
        html = new TextView(this);
        commands.add("");
        commands.add("");
        aiButton = (AIButton) findViewById(R.id.micButton);
        mList = (ListView) findViewById(R.id.listView1);
        Log.e("Language1" , ""+language.getString("lang", ""));
        Log.e("Base language", ""+getBaseContext().getResources().getConfiguration().locale.getDisplayName());
        if(language.getString("lang", "").contains("fr")){
            config = new AIConfiguration(Config.ACCESS_TOKEN,
                    AIConfiguration.SupportedLanguages.French,
                    AIConfiguration.RecognitionEngine.System);
            Locale locale = new Locale("FR");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getApplicationContext().getResources().updateConfiguration(config, null);

        }
        else{
            config = new AIConfiguration(Config.ACCESS_TOKEN,
                    AIConfiguration.SupportedLanguages.English,
                    AIConfiguration.RecognitionEngine.System);
        }
        Log.e("Base language", ""+getBaseContext().getResources().getConfiguration().locale.getDisplayName());
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
    protected void onStart() {
        super.onStart();

        checkAudioRecordPermission();
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
                Log.e("Language2" , ""+language.getString("lang", ""));
                if(language.getString("lang", "").contains("fr")){
                    config = new AIConfiguration(Config.ACCESS_TOKEN,
                            AIConfiguration.SupportedLanguages.French,
                            AIConfiguration.RecognitionEngine.System);
                    getBaseContext().getResources().getConfiguration().locale.setDefault(new Locale("FR", "FRANCE"));
                }
                else{
                    config = new AIConfiguration(Config.ACCESS_TOKEN,
                            AIConfiguration.SupportedLanguages.English,
                            AIConfiguration.RecognitionEngine.System);
                }
                config.setRecognizerStartSound(getResources().openRawResourceFd(R.raw.test_start));
                config.setRecognizerStopSound(getResources().openRawResourceFd(R.raw.test_stop));
                config.setRecognizerCancelSound(getResources().openRawResourceFd(R.raw.test_cancel));
                aiButton.initialize(config);
                aiButton.setResultsListener(AIButtonSampleActivity.this);
                final Result result = response.getResult();
                Log.e("Intent name", result.getMetadata().getIntentName());
                if ((signed_in == true)&&(result.getMetadata().getIntentName().contains("Sign in"))){
                    result.getFulfillment().setSpeech(getResources().getString(R.string.signed_in));
                    commands.add(result.getFulfillment().getSpeech());
                    TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                    i++;
                }
                if ((signed_in == true)&&(result.getMetadata().getIntentName().contains("Sign out"))){
                    commands.add(result.getFulfillment().getSpeech());
                    TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                    i++;
                    signed_in = false;
                    show(result);
                    return;
                }
                if ((signed_in == false)&&(result.getMetadata().getIntentName().contains("Sign out"))){
                    result.getFulfillment().setSpeech(getResources().getString(R.string.signed_out));
                    commands.add(result.getFulfillment().getSpeech());
                    TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                    i++;
                    show(result);
                    return;
                }
                if ((commands.get(i-1).contains("Ok, please give me"))||(commands.get(i-1).contains("D'accord, donnez-moi votre numéro d'identification."))){
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
                        case "D'accord, donnez-moi votre numéro d'identification.":
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
                        String text = getResources().getString(R.string.welcome_usr);
                        text = text.replace("name",HoC2.getUser_name());
                        result.getFulfillment().setSpeech(text);
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        signed_in = true;
                        exist = false;
                    }
                    else if (exist == false){
                        result.getFulfillment().setSpeech(getResources().getString(R.string.sign_in_issue));
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                    }
                    commands.add(result.getFulfillment().getSpeech());
                    i++;
                    show(result);
                    return;
                }
                if ((signed_in == true)||(result.getMetadata().getIntentName().equals("Sign in"))
                        ||(result.getAction().contains("smalltalk"))||(result.getMetadata().getIntentName().equals("web.search"))
                        ||(result.getMetadata().getIntentName().contains("weather"))
                        ||(result.getMetadata().getIntentName().contains("reminders"))
                        ||(result.getMetadata().getIntentName().equals("customize.lang"))){
                    if (result.getAction().contains("smalltalk")){
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                    }
                    if (result.getMetadata().getIntentName().equals("customize.lang")){
                        SharedPreferences.Editor editor2 = language.edit();
                        String saved_lang = language.getString("lang", "");
                        if(saved_lang.equals("en")){
                            editor2.putString("lang", "fr");
                            editor2.commit();
                            Log.e("Language after", language.getString("lang", ""));
                        }
                        else{
                            editor2.putString("lang", "en");
                            editor2.commit();
                            Log.e("Language after", language.getString("lang", ""));
                        }
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                    }
                    if (result.getMetadata().getIntentName().equals("web.search")){
                        result.getFulfillment().setSpeech(getResources().getString(R.string.processing));
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        String toSearch = result.getParameters().get("q").toString();
                        toSearch = toSearch.replaceAll("\"", "");
                        Intent viewSearch = new Intent(Intent.ACTION_WEB_SEARCH);
                        viewSearch.putExtra(SearchManager.QUERY, toSearch);
                        startActivity(viewSearch);
                    }
                    if (result.getMetadata().getIntentName().contains("reminders.")){
                        String eve_name = "";
                        String eve_date = "";
                        String eve_recc = "";
                        if (result.getMetadata().getIntentName().equals("reminders.add")){
                            result.getFulfillment().setSpeech(getResources().getString(R.string.processing));
                            try {
                                eve_name = result.getParameters().get("name").toString();
                            }catch (Exception e){
                                Log.e("eve_name", ""+e);
                            }
                            try {
                                eve_date = result.getParameters().get("date-time").toString();
                            }catch (Exception e){
                                eve_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                            }
                            try {
                                eve_recc = result.getParameters().get("recurrence").toString();
                            }catch (Exception e){
                                Log.e("eve_recc", ""+e);
                            }
                            Log.e("Data", eve_name+" <> "+eve_date+" <> "+eve_recc);
                            eve_date = eve_date.replaceAll("\"", "");
                            eve_name = eve_name.replaceAll("\"", "");
                            eve_recc = eve_recc.replaceAll("\"", "");
                            calendar_event(eve_name, null, eve_date, eve_recc, result);
                        }
                    }
                    if (result.getAction().contains("weather")){
                        result.getFulfillment().setSpeech(getResources().getString(R.string.processing));
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        String location = "Ottawa";
                        try{
                            location = result.getParameters().get("location").toString();
                            location = location.replaceAll("\"", "").trim();
                            location = location.replaceAll(":", " ");
                            location = location.substring(location.indexOf(" "), location.length()-1);
                        }catch (Exception e){
                            Log.e("Error forcast location", ""+e);
                        }
                        if (location == "Ottawa"){
                            try{
                                location = result.getParameters().get("address").toString();
                                location = location.replaceAll("\"", "").trim();
                                location = location.replaceAll(":", " ");
                                location = location.substring(location.indexOf(" "), location.length()-1);
                            }catch (Exception e){
                                Log.e("Error forcast location", ""+e);
                            }
                        }
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date1 = new Date();
                        String date = dateFormat.format(date1);
                        try{
                            date = result.getParameters().get("date-time").toString();
                            date = date.replaceAll("\"", "");
                        }catch (Exception e){
                            Log.e("Error forcast date", ""+e);
                        }
                        new getJSON().execute(location);
                        }
                    if (result.getMetadata().getIntentName().equals("Sign in"))
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());

                    if (result.getMetadata().getIntentName().contains("time")){
                        Log.e("time","");
                        Calendar rightNow = Calendar.getInstance();
                        String  currentTime = getResources().getString(R.string.time).replace("time", rightNow.get(Calendar.HOUR)+":"+rightNow.get(Calendar.MINUTE));
                        Log.e("time", currentTime);
                        result.getFulfillment().setSpeech(currentTime.toString());
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                    }
                    if (result.getMetadata().getIntentName().contains("member.bus")){
                        result.getFulfillment().setSpeech(getResources().getString(R.string.wait));
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        Calendar sCalendar = Calendar.getInstance();
                        int minute = sCalendar.getTime().getMinutes();
                        int minute_new = (((int)(minute/10))+1)*10;
                        if(minute_new-minute==1){
                            result.getFulfillment().setSpeech(getResources().getString(R.string.bus).replace("xxx", ""+(minute_new-minute)));
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        }
                        else {
                            result.getFulfillment().setSpeech(getResources().getString(R.string.buss).replace("xxx", ""+(minute_new-minute)));
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        }
                    }
                    if (result.getMetadata().getIntentName().contains("member.now")){
                        result.getFulfillment().setSpeech(getResources().getString(R.string.wait));
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        AsyncTask<String, Void, String> s = null;
                        String data="";
                        List <String> msgs = new ArrayList<>();
                        msgs.add("The House is currently sitting. Current Member Speaking: The Honourable Pierre Poilievre Conservative.");
                        msgs.add("The House is currently sitting. Current Member Speaking: The Speaker Geoff Regan Liberal.");
                        msgs.add("The House is currently sitting. Current Member Speaking: Carol Hughes NDP.");
                        msgs.add("The House is currently sitting. Current Member Speaking: Mark Gerretsen Liberal.");
                        int rand = ThreadLocalRandom.current().nextInt(0, 4);
                        result.getFulfillment().setSpeech(msgs.get(rand));
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                    }
                    if (result.getMetadata().getIntentName().contains("member.menu")){
                        String dayLongName = result.getFulfillment().getSpeech();
                        if (dayLongName.length()==0){
                            result.getFulfillment().setSpeech(getResources().getString(R.string.db_empty));
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                            commands.add(result.getFulfillment().getSpeech());
                            i++;
                            show(result);
                            return;
                        }
                        result.getFulfillment().setSpeech(getResources().getString(R.string.wait));
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
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
                            result.getFulfillment().setSpeech(getResources().getString(R.string.caf_closed));
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                            commands.add(result.getFulfillment().getSpeech());
                            i++;
                            show(result);
                            return;
                        }
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
                            result.getFulfillment().setSpeech(getResources().getString(R.string.db_empty));
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        }
                        else{
                            String text = getResources().getString(R.string.caf).replace("day", dayLongName);
                            text = text.replace("food", HoC_m.getDescription());
                            result.getFulfillment().setSpeech(text);
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        }
                    }
                    if (result.getMetadata().getIntentName().contains("member.expense")){
                        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                        Date date = new Date();
                        String s="You have 2 expense claims:\n";
                        s = s+"1. Expense claim for in riding mileage submitted the "+dateFormat.format(date)+", status processed amount of 324 credited to your account\n" +
                                "2. Expense claim for trip to Winnipeg from Ottawa status pending explanation, do you want an officer to call you about this one.";
                        result.getFulfillment().setSpeech(s);
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                    }
                    if (result.getMetadata().getIntentName().contains("visitor.meet.")||(result.getMetadata().getIntentName().equals("visitor.meet.remindd"))){
                        result.getFulfillment().setSpeech(getResources().getString(R.string.wait));
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        String ss = "";
                        try {
                            ss = result.getParameters().get("meeting_n").toString();
                            ss = ss.replaceAll("\"", "");
                            ss = ss.replaceAll( " ", "");
                        }catch (Exception e){
                        }
                        AsyncTask<String, Void, String> s = null;
                        try{
                            if(ss!= "") {
                                s = new Fetch.HttpAsyncTask().execute(uri + "Meetings/GetMeetingsByName/" + HoC2.getId() + "/" + ss);
                                Log.e("Link" ,uri+"Meetings/GetMeetingsByName/"+HoC2.getId()+"/"+ss);
                            }
                            else{
                                s = new Fetch.HttpAsyncTask().execute(uri+"Meetings/GetMeetingsByIdVisitor/"+HoC2.getId());
                                Log.e("Link" ,uri+"Meetings/GetMeetingsByIdVisitor/"+HoC2.getId());
                            }
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
                            result.getFulfillment().setSpeech(getResources().getString(R.string.db_empty));
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        }
                        else if(min_ind == -1){
                            result.getFulfillment().setSpeech(getResources().getString(R.string.db_empty));
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        }
                        else{
                            if(result.getMetadata().getIntentName().contains("visitor.meet.room")){
                                URL1 = HoC_m.get(min_ind).getRoom().getImg();
                                fotoView = new ImageView(AIButtonSampleActivity.this);
                                fotoView.setTag(URL1);
                                new DownloadImagesTask().execute(fotoView);
                                result.getFulfillment().setSpeech(getResources().getString(R.string.plan));
                                TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
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
                            if(result.getMetadata().getIntentName().contains("visitor.meet.remindd")) {
                                result.getFulfillment().setSpeech(getResources().getString(R.string.processing));
                                TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                                String eve_name, eve_date, eve_note;

                                eve_date = HoC_m.get(min_ind).getDate();
                                Log.e("eve_date", ""+eve_date.length());
                                eve_name = HoC_m.get(min_ind).getName();
                                eve_note = HoC_m.get(min_ind).getDescription();
                                Log.e("Data", eve_name+" <> "+eve_date);
                                eve_date = eve_date.replaceAll("\"", "");
                                eve_name = eve_name.replaceAll("\"", "");
                                calendar_event(eve_name, eve_note, eve_date, null, result);
                            }
                            if(result.getMetadata().getIntentName().contains("visitor.meet.last")){
                                String sp= getResources().getString(R.string.next_meeting);
                                try{
                                    sp = sp.replace(" date", HoC_m.get(min_ind).getDate());
                                    sp = sp.replace(" chair_name", " "+HoC_m.get(min_ind).getChair().getName()+" ");
                                    sp = sp.replace(" room_name ", " "+HoC_m.get(min_ind).getRoom().getName()+" ");
                                    sp = sp.replace(" room_building ", " "+HoC_m.get(min_ind).getRoom().getBuilding()+" ");
                                    sp = sp.replace(" room_floor ", " "+HoC_m.get(min_ind).getRoom().getFloor()+" ");
                                    sp = sp.replace(" description", " "+HoC_m.get(min_ind).getDescription()+" ");

//                                    sp= "Your next meeting is on "+HoC_m.get(min_ind).getDate()+" with "+HoC_m.get(min_ind).getChair().getName()
//                                        +", "+HoC_m.get(min_ind).getRoom().getName()+" in "+HoC_m.get(min_ind).getRoom().getBuilding()+", floor number "+HoC_m.get(min_ind).
//                                        getRoom().getFloor()+" and it's about "+HoC_m.get(min_ind).getDescription();
                                }
                                catch (Exception e){
                                    sp = getResources().getString(R.string.db_empty);
                                }
                                result.getFulfillment().setSpeech(sp);
                                TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                            }
                            if(result.getMetadata().getIntentName().contains("visitor.meet.held")){
//                                String sp = getResources().getString(R.string.meet_room);
//                                sp = sp.replace("room_name", HoC_m.get(min_ind).getRoom().getName());
                                result.getFulfillment().setSpeech(getResources().getString(R.string.meet_room).replace("room_name", HoC_m.get(min_ind).getRoom().getName()));
                                TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                            }

                            if(result.getMetadata().getIntentName().contains("visitor.meet.when")){
                                String sp = getResources().getString(R.string.meet_brief);
                                sp = sp.replace("date", HoC_m.get(min_ind).getDate());
                                sp = sp.replace("description", HoC_m.get(min_ind).getDescription());
                                result.getFulfillment().setSpeech(sp);
                                TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                            }

                            if(result.getMetadata().getIntentName().contains("visitor.meet.who")){
                                result.getFulfillment().setSpeech(getResources().getString(R.string.meet_chair).replace("chair_name", HoC_m.get(min_ind).getChair().getName()));
                                TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                            }
                            if(result.getMetadata().getIntentName().contains("visitor.meet.more")){
                                urll = HoC_m.get(min_ind).getUri();
                                result.getFulfillment().setSpeech(getResources().getString(R.string.more));
                                TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
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
                        result.getFulfillment().setSpeech(getResources().getString(R.string.wait));
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
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
                            result.getFulfillment().setSpeech(getResources().getString(R.string.switch_prob));
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        }
                        else{
                            String sp = getResources().getString(R.string.office_switch);
                            try{
                                sp = sp.replace("office_desc", HoC_offices.get(0).getDescription());
                                sp = sp.replace("office_name", HoC_offices.get(0).getSwitcher().getName());
                                result.getFulfillment().setSpeech(sp);
                                TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());}
                            catch(Exception e){
                                result.getFulfillment().setSpeech(getResources().getString(R.string.db_empty));
                                TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
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
                        result.getFulfillment().setSpeech(getResources().getString(R.string.wait));
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
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
                            result.getFulfillment().setSpeech(getResources().getString(R.string.switch_prob));
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
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
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
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
                        result.getFulfillment().setSpeech(getResources().getString(R.string.wait));
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
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
                            result.getFulfillment().setSpeech(getResources().getString(R.string.db_empty));
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        }
                        //Todo add the french lanquage here. need some work
                        else{
                            String sp = "Techninican "+ind+" has performed the following:\n";
                            for (int j=0; j<HoC_sw.size(); j++){
                                sp = sp+(j+1)+". On "+parse_from_db(HoC_sw.get(j).getDate())+", "+HoC_sw.get(j).getDescription()+" on switch "+
                                        HoC_sw.get(j).getswitcher().getName()+".\n";
                            }
                            result.getFulfillment().setSpeech(sp);
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
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
                        result.getFulfillment().setSpeech(getResources().getString(R.string.wait));
                        TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
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
                            result.getFulfillment().setSpeech(getResources().getString(R.string.db_empty));
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        }
                        else{
                            int min_ind = sort_interv(HoC_sw);
                            String sp = "Switch "+ind+" serviced by "+HoC_sw.get(min_ind).gettechnician().getFull_name()+", his id is "+
                                    HoC_sw.get(min_ind).gettechnician().getId()+" on "+HoC_sw.get(min_ind).getDate()+" for "+HoC_sw.
                                    get(min_ind).getDescription()+".";
                            result.getFulfillment().setSpeech(sp);
                            TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
                        }
                        commands.add(result.getFulfillment().getSpeech());
                        i++;
                    }
                    commands.add(result.getFulfillment().getSpeech());
                    i++;
                    show(result);
            }
                else {
                            result.getFulfillment().setSpeech(getResources().getString(R.string.sign_in));
                    show(result);
                    TTS.speak(language.getString("lang", ""),result.getFulfillment().getSpeech());
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
    public static String parser(Date x){
        SimpleDateFormat spf = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        String newDateString = spf.format(x);
        return newDateString;
    }
    public String parse_from_db(String x){
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormat targetFormat = new SimpleDateFormat("d MMMM yyyy HH:mm");
        Date date = null;
        try {
            date = originalFormat.parse(x);
        } catch (ParseException e) {
            Log.e("3asba", ""+e);
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
    public forecast string_json_W(String res) throws JSONException
    {
        forecast Hoc = new forecast();
        forecast jsonArr = new Gson().fromJson(res, forecast.class);
        if (jsonArr != null) {
            Hoc.setWeather(new weather[]{jsonArr.getWeather()[0]});
            Hoc.setTemp_hum_press(jsonArr.getTemp_hum_press());
            Hoc.setWind(jsonArr.getWind());
            Hoc.setSys(jsonArr.getSys());
            Hoc.setVisibility(jsonArr.getVisibility());
            Hoc.setName(jsonArr.getName());
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

            }catch(Exception e){
            }
            return bmp;
        }
    }
    public int sort (List<Meetings> abc) {
        int k=-1;
        DateFormat format = new SimpleDateFormat("d MMMM yyyy mm:ss");
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
        try{
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
        }catch (Exception e){
            k = 1;
        }
        return k;
    }
    public int sort_interv(List<Interventions> abc) {
        int k=-1;
        DateFormat format = new SimpleDateFormat("d MMMM yyyy mm:ss");
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
    class getJSON extends AsyncTask<String, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }
            @Override
            protected Void doInBackground(String... params) {
                try {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+params[0].trim()+"&APPID=ea574594b9d36ab688642d5fbeab847e");
                    Log.e("URL", url.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";
                    while((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();
                    data_weather = new JSONObject(json.toString());
                    if(data_weather.getInt("cod") != 200) {
                        System.out.println("Cancelled");
                        return null;
                    }
                } catch (Exception e) {
                    System.out.println("Exception "+ e.getMessage());
                    return null;
                }
                return null;
            }
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            protected void onPostExecute(Void Void) {
                if(data_weather!=null){
                    Log.e("my weather received",data_weather.toString());
                    forecast f = null;
                    try {
                        f = string_json_W(data_weather.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    f.getWeather()[0].setIcon("http://openweathermap.org/img/w/"+f.getWeather()[0].getIcon()+".png");
                    f.getTemp_hum_press().setTemp(f.getTemp_hum_press().getTemp()-273.15);
                    f.getTemp_hum_press().setTemp_max(f.getTemp_hum_press().getTemp_max()-273.15);
                    f.getTemp_hum_press().setTemp_min(f.getTemp_hum_press().getTemp_min()-273.15);
                    f.getWind().setSpeed(f.getWind().getSpeed()*3.6);
                    DateFormat sdf = new SimpleDateFormat("dd MMMM HH:mm");
                    String sunrise = sdf.format(new Date(f.getSys().getSunrise()* 1000L));
                    String sunset  = sdf.format(new Date(f.getSys().getSunset() * 1000L));
                    java.text.DecimalFormat df = new java.text.DecimalFormat("0.#");
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(AIButtonSampleActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(AIButtonSampleActivity.this);
                    }

                    builder.setView(R.layout.forecast_w);
                    LayoutInflater inflater = AIButtonSampleActivity.this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.forecast_w, null);
                    builder.setView(dialogView);
                    loc = (TextView) dialogView.findViewById(R.id.location);
                    TextView temp = (TextView) dialogView.findViewById(R.id.temp);
                    TextView temp_min = (TextView) dialogView.findViewById(R.id.tempMin);
                    TextView temp_max = (TextView) dialogView.findViewById(R.id.tempMax);
                    TextView desc_w = (TextView) dialogView.findViewById(R.id.descrWeather);
                    TextView wind_speed = (TextView) dialogView.findViewById(R.id.windSpeed);
                    TextView wind_deg = (TextView) dialogView.findViewById(R.id.windDeg);
                    TextView humidity = (TextView) dialogView.findViewById(R.id.humidity);
                    TextView pressure = (TextView) dialogView.findViewById(R.id.pressure);
                    TextView visibility = (TextView) dialogView.findViewById(R.id.visibility);
                    ImageView imageWea = (ImageView) dialogView.findViewById(R.id.imgWeather);
                    TextView sun_r = (TextView) dialogView.findViewById(R.id.sunrise);
                    TextView sun_s = (TextView) dialogView.findViewById(R.id.sunset);
                    desc_w.setText(f.getWeather()[0].getDescription());
                    temp_min.setText(""+df.format(f.getTemp_hum_press().getTemp_min())+"°");
                    temp_max.setText(""+df.format(f.getTemp_hum_press().getTemp_max())+"°");
                    temp.setText(""+df.format(f.getTemp_hum_press().getTemp()));
                    wind_speed.setText(""+df.format(f.getWind().getSpeed())+" Km/h");
                    wind_deg.setText(""+f.getWind().getDeg()+"°");
                    visibility.setText(""+(df.format(f.getVisibility()/1000))+" Km");
                    humidity.setText(f.getTemp_hum_press().getHumidity()+" %");
                    pressure.setText(""+f.getTemp_hum_press().getPressure()+" mbar");
                    sun_r.setText(sunrise);
                    sun_s.setText(sunset);
                    loc.setText(f.getName()+", "+f.getSys().getCountry());
                    switch(f.getWeather()[0].getMain()) {
                        case "Clouds":
                            imageWea.setImageResource(R.drawable.clouds);
                            break;
                        case "Clear":
                            imageWea.setImageResource(R.drawable.clear);
                            break;
                        case "Rain":
                            imageWea.setImageResource(R.drawable.raining);
                            break;
                        case "Snow":
                            imageWea.setImageResource(R.drawable.snow);
                            break;
                    }
                    builder.setTitle("Forecast")
                            .setIcon(R.drawable.fff)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    final AlertDialog dialog = builder.create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2072ac")));
                    dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#ffffff"));
                        }
                    });
                    dialog.show();
                }
            }
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
    public void calendar_event(String name, String note, String date_s, String recc, Result result){
        //Date date = parse(date_s);
        SimpleDateFormat f = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        long milliseconds = 0;
        String dateFormatted = "";
        try {
            Date d = f.parse(date_s);
            d.setHours(d.getHours()-1);
            milliseconds = d.getTime();
            Date date = null;
            dateFormatted = f.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e("Dates compare", date_s+" <> "+dateFormatted);
        String unity, notif;
        unity = "";
        notif = "";
        try{
            unity = result.getParameters().get("unity").toString();
            unity = unity.replaceAll("\"", "");
            Log.e("unity", unity);
        }catch (Exception e){
            Log.e("unity", ""+e);
        }
        try{
            notif = result.getParameters().get("notify").toString();
            notif = notif.replaceAll("\"", "");
            Log.e("notify", notif   );
        }catch (Exception e){
            Log.e("notify", ""+e);
        }
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("title", name);
        intent.putExtra("note", note);
        if ((unity.equals("hour"))&&(Integer.parseInt(notif)!=0)){
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, milliseconds);
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, (milliseconds + (3600 * 1000)));
        }
        if ((unity.equals("minute"))&&(Integer.parseInt(notif)!=0)){
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, milliseconds);
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, (milliseconds + (3600 * 1000)));
        }
        else{
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, milliseconds + (3600 * 1000));
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, milliseconds + (2*3600 * 1000));
        }
        //Toast.makeText(AIButtonSampleActivity.this, ""+notif, Toast.LENGTH_LONG).show();
        startActivity(intent);
    }
    public static Date parse(String d) {
        Date date  = null;
        if (d != null) {
            for (String parse : formats) {
                SimpleDateFormat sdf = new SimpleDateFormat(parse);
                sdf.setLenient(false);
                try {
                    d = d.replace("\"", "");
                    date = sdf.parse(d);
                    break;
                } catch (ParseException e) {
                }
            }
        }
        return date;
    }
}