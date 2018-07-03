package com.born2go.lazzybee.db.api;

import com.born2go.lazzybee.gdatabase.server.dataServiceApi.DataServiceApi;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.DataServiceApi.GetVocaById;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.DataServiceApi.GetVocaByQ;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.DataServiceApi.ListVoca;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.GroupVoca;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.VocaCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;
import java.util.List;

/*
 *  connect to google database by endpoints client libs
 */
public class ConnectGdatabase {

    // constructor
    public ConnectGdatabase() {

    }

    private DataServiceApi api;
    final String applicationName = "Lazzybee";


    public DataServiceApi getDataServiceApi() {
        HttpTransport transport;
        JsonFactory gsonFactory;
        HttpRequestInitializer initializer = null;
        DataServiceApi apiService = null;
        try {
            gsonFactory = new GsonFactory();
            //Setup DataSevices Api
            apiService = new DataServiceApi.Builder(AndroidHttp.newCompatibleTransport(), gsonFactory,
                    initializer)
                    .setRootUrl(DataServiceApi.DEFAULT_ROOT_URL).setApplicationName(applicationName)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiService;
    }

    /**
     * get vocabulary in database google by id
     *
     * @param valueId : id of vocabulary you want to search in google
     */
    public Voca _getGdatabase_byID(Long valueId) {
        GetVocaById apiInst;
        Voca word = null;
        try {
            api = getDataServiceApi();
            apiInst = api.getVocaById(valueId);
            word = apiInst.execute();
            System.out.println("word" + word.toPrettyString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return word;
    }

    /**
     * get vocabulary in database google by q(question)
     *
     * @param q question of vocabulary
     */
    public Voca _getGdatabase_byQ(String q) {
        GetVocaByQ apiInst;
        Voca word = null;
        try {
            api = getDataServiceApi();
            apiInst = api.getVocaByQ(q);
            word = apiInst.execute();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return word;

    }

    public GroupVoca _getGroupVoca(long id) {
        DataServiceApi.GetGroupVoca apiInst;
        GroupVoca groupVoca = null;
        try {
            api = getDataServiceApi();
            apiInst = api.getGroupVoca(id);
            groupVoca = apiInst.execute();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return groupVoca;

    }

}
