package jpdftwist.gui.component.treetable.row;

class PageID {
    private static Integer id;

    static {
        PageID.id = 0;
    }

    public String getStringAsId() {
        PageID.id++;
        return String.format("%010d", PageID.id);
    }
}
