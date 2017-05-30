package zhu.appstudmaptest;

/**
 * Created by lenovo on 2017/5/30.
 */
public class Bar {
    private String imageUrl;
    private String label;

    public Bar(String label, String imageUrl){
        this.label = label;
        this.imageUrl = imageUrl;

    }
    public String getLabel() {
        return label;
    }

    public String getImageUrl() {
        return imageUrl;
    }


}
