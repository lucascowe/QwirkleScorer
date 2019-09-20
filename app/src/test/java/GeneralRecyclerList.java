public class GeneralRecyclerList {
    private String data1 = "";
    private String data2 = "";
    private String data3 = "";
    private String data4 = "";

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    private Boolean selected = false;

    public GeneralRecyclerList() {
        // do nothing
    }

    public GeneralRecyclerList(String d1) {
        this.data1 = d1;
    }

    public GeneralRecyclerList(String d1, String d2) {
        this.data1 = d1;
        this.data2 = d2;
    }

    public GeneralRecyclerList(String d1, String d2, String d3) {
        this.data1 = d1;
        this.data2 = d2;
        this.data3 = d3;
    }

    public GeneralRecyclerList(String d1, String d2, String d3, String d4) {
        this.data1 = d1;
        this.data2 = d2;
        this.data3 = d3;
        this.data4 = d4;
    }

    public GeneralRecyclerList(String data1, String data2, String data3, String data4, Boolean selected) {
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
        this.data4 = data4;
        this.selected = selected;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public String getData4() {
        return data4;
    }

    public void setData4(String data4) {
        this.data4 = data4;
    }
}
