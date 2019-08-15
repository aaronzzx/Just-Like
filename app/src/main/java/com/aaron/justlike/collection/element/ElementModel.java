package com.aaron.justlike.collection.element;

import com.aaron.justlike.common.bean.Collection;
import com.aaron.justlike.common.bean.Element;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.main.preview.PreviewActivity;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ElementModel implements IElementContract.M<Image> {

    private ExecutorService mExecutorService;

    ElementModel() {
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void queryImage(String title, Callback<Image> callback) {
        mExecutorService.execute(() -> {
            List<Element> elements = LitePal.where("title = ?", title).find(Element.class);
            Collections.sort(elements, (o1, o2) -> (int) (o2.getCreateAt() - o1.getCreateAt()));
            List<Image> imageList = new ArrayList<>();
            for (Element element : elements) {
                Image image = new Image(element.getPath());
                image.setEventFlag(PreviewActivity.DELET_EVENT);
                imageList.add(image);
            }
            callback.onResponse(imageList);
        });
    }

    @Override
    public void insertImage(String title, int size, List<String> list, Callback<Image> callback) {
        List<Image> imageList = new ArrayList<>();
        mExecutorService.execute(() -> {
            Collection info = new Collection();
            info.setTotal(size + list.size());
            info.setPath(list.get(list.size() - 1));
            info.setCreateAt(System.currentTimeMillis());
            info.updateAll("title = ?", title);

            for (String path : list) {
                Element element = new Element();
                element.setTitle(title);
                element.setPath(path);
                element.setCreateAt(System.currentTimeMillis());
                element.save();

                Image image = new Image(path);
                image.setDate(String.valueOf(System.currentTimeMillis()));
                imageList.add(image);
            }
            callback.onResponse(imageList);
        });
    }

    @Override
    public void deleteImage(String title, String path) {
        mExecutorService.execute(() -> {
            // updateForAdd element
            LitePal.getDatabase().delete("Element", "path = ?", new String[]{path});
            List<Element> elements = LitePal.where("title = ?", title).find(Element.class);

            // updateForAdd collection
            List<Collection> collections = LitePal.where("title = ?", title).find(Collection.class);
            int count = collections.get(0).getTotal();
            if (count > 1) {
                count--;
                Collection collection = new Collection();
                collection.setTotal(count);
                collection.setPath(elements.get(elements.size() - 1).getPath());
                collection.updateAll("title = ?", title);
            } else {
                LitePal.getDatabase().delete("Collection", "title = ?", new String[]{title});
            }
        });
    }
}
