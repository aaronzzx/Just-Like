package com.aaron.justlike.app.collection.model;

import com.aaron.justlike.app.collection.entity.Collection;
import com.aaron.justlike.app.collection.entity.Element;
import com.aaron.justlike.app.main.entity.Image;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElementModel implements IElementModel<Image> {

    private ExecutorService mExecutorService;

    public ElementModel() {
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void queryImage(String title, Callback<Image> callback) {
        mExecutorService.execute(() -> {
            List<Element> elements = LitePal.where("title = ?", title).find(Element.class);
            List<Image> imageList = new ArrayList<>();
            for (Element element : elements) {
                imageList.add(new Image(element.getPath()));
            }
            callback.onResponse(imageList);
        });
    }

    @Override
    public void deleteImage(String title, String path) {
        mExecutorService.execute(() -> {
            LitePal.getDatabase().delete("Element", "path = ?", new String[]{path});
            List<Collection> collections = LitePal.where(title).find(Collection.class);
            int count = collections.get(0).getTotal();
            if (count > 1) {
                count -= 1;
                Collection collection = new Collection();
                collection.setTotal(count);
                collection.updateAll("title = ?", title);
            } else {
                LitePal.getDatabase().delete("Collection", "title = ?", new String[]{title});
            }
        });
    }
}
