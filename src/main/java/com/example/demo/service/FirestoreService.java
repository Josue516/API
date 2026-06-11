package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.SetOptions;
import com.example.demo.models.FirestoreEntity;

@Service
public class FirestoreService {

    @Autowired
    private Firestore firestore;

    // CREATE
    public <T> String create(String collection, T obj) throws Exception {
        return firestore.collection(collection).add(obj).get().getId();
    }

    // GET ALL
    public <T> List<T> getAll(String collection, Class<T> clazz) throws Exception {

        List<T> list = new ArrayList<>();

        ApiFuture<QuerySnapshot> snapshot =
                firestore.collection(collection).get();

        for (DocumentSnapshot doc : snapshot.get().getDocuments()) {

            T obj = doc.toObject(clazz);

            if (obj != null) {

                if (obj instanceof FirestoreEntity entity) {
                    entity.setId(doc.getId());
                }

                list.add(obj);
            }
        }

        return list;
    }

    // FILTER (Para cartelera)
    public <T extends FirestoreEntity> List<T> getByField(
            String collection,
            String field,
            Object value,
            Class<T> clazz) throws Exception {

        List<T> list = new ArrayList<>();

        Query query = firestore.collection(collection)
                .whereEqualTo(field, value);

        ApiFuture<QuerySnapshot> snapshot = query.get();

        for (DocumentSnapshot doc : snapshot.get().getDocuments()) {

            T obj = doc.toObject(clazz);

            if (obj != null) {
                obj.setId(doc.getId());
                list.add(obj);
            }
        }

        return list;
    }

    // UPDATE parcial
    public <T> void update(String collection, String id, T obj) throws Exception {

        firestore.collection(collection)
                .document(id)
                .set(obj, SetOptions.merge());
    }

    // DELETE físico (o puedes cambiar a lógico)
    public void delete(String collection, String id) throws Exception {
        firestore.collection(collection).document(id).delete();
    }

    // GET BY ID
    public <T extends FirestoreEntity> T getById(
            String collection,
            String id,
            Class<T> clazz) throws Exception {

        DocumentSnapshot doc = firestore
                .collection(collection)
                .document(id)
                .get()
                .get();
        

        T obj = doc.toObject(clazz);
        if (obj != null) {
            obj.setId(doc.getId());
        }

        return obj;
    }
    //Borrado suave
    public void softDelete(
            String collection,
            String id,
            String field,
            Object value) throws Exception {

        Map<String, Object> data = new HashMap<>();
        data.put(field, value);

        firestore.collection(collection)
                .document(id)
                .update(data);
    }
}