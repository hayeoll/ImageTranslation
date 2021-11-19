package com.example.imagetranslation;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.*;

public class Vision {
    private FirebaseFunctions mFunctions;
    public String text;

    public String OCR(String base64encoded) {
        mFunctions = FirebaseFunctions.getInstance();
        // 요청 오브젝트 생성
        JsonObject request = new JsonObject();
        // 이미지 추가
        JsonObject image = new JsonObject();
        image.add("content", new JsonPrimitive(base64encoded));
        request.add("image", image);
        // feature 추가 및 배열 생성
        JsonObject feature = new JsonObject();
        feature.add("type", new JsonPrimitive("TEXT_DETECTION"));
        JsonArray features = new JsonArray();
        features.add(feature);
        request.add("features", features);



        //함수 호출
        annotateImage(request.toString())
                .addOnCompleteListener(new OnCompleteListener<JsonElement>() {
                    @Override
                    public void onComplete(@NonNull Task<JsonElement> task) {
                        if (!task.isSuccessful()) {
                            // Task failed with an exception
                            Log.e("OCR", "OCR태스크오류!!!");
                        } else {
                            // Task completed successfully
                            JsonObject annotation = task.getResult().getAsJsonArray().get(0).getAsJsonObject().get("fullTextAnnotation").getAsJsonObject();
                            text = annotation.get("text").getAsString();
                            Log.i("OCR", "OCR 성공"+text);
                        }
                    }
                });

        return text;
    }

    private Task<JsonElement> annotateImage(String requestJson) {
        return mFunctions
                .getHttpsCallable("annotateImage")
                .call(requestJson)
                .continueWith(new Continuation<HttpsCallableResult, JsonElement>() {
                    @Override
                    public JsonElement then(@NonNull Task<HttpsCallableResult> task) {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down
                        return JsonParser.parseString(new Gson().toJson(task.getResult().getData()));
                    }
                });
    }

}
