package amadeuslms.amadeus.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.List;

import amadeuslms.amadeus.lists.SubjectList;
import amadeuslms.amadeus.models.SubjectModel;
import amadeuslms.amadeus.models.UserModel;

/**
 * Created by zambom on 30/06/17.
 */

public class SubjectCacheController {

    private static final String SUBJECT_PREFERENCE_KEY = "SUBJECT_PREFERENCE_KEY";

    private static SubjectList model;

    public static SubjectList getSubjectCache(Context context) {
        try {
            if (model != null) {
                return model;
            }

            SharedPreferences sharedPreferences = SharedPreferencesContainer.getSharedPreferences(context);

            if(sharedPreferences.contains(SUBJECT_PREFERENCE_KEY)){
                String json = sharedPreferences.getString(SUBJECT_PREFERENCE_KEY, "");

                if(!TextUtils.isEmpty(json)){
                    SubjectList subjectFromJson = new Gson().fromJson(json, SubjectList.class);

                    if(subjectFromJson != null){
                        model = subjectFromJson;
                        return model;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static boolean hasSubjectCache(Context context){
        try{
            if(model != null){
                return true;
            }

            SubjectList subjects = getSubjectCache(context);

            if(subjects != null){
                return true;
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return false;
    }

    public static void setSubjectCache(Context context, SubjectList subjects){
        try{
            String json = new Gson().toJson(subjects);

            SharedPreferences.Editor editor = SharedPreferencesContainer.getEditor(context);
            editor.putString(SUBJECT_PREFERENCE_KEY, json);
            editor.commit();

            model = subjects;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void removeSubjectCache(Context context){
        try{
            SharedPreferences.Editor editor = SharedPreferencesContainer.getEditor(context);
            editor.remove(SUBJECT_PREFERENCE_KEY);
            editor.commit();

            model = null;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
