package kr.wdream.Wdream.Cell;

/**
 * Created by deobeuldeulim on 2016. 12. 6..
 */

public class NotiVO {
    private String notice_no;
    private String subject;
    private String context;
    private String update_dt;

    public NotiVO(String notice_no, String subject, String context, String update_dt){
        this.notice_no = notice_no;
        this.subject = subject;
        this.context = context;
        this.update_dt = update_dt;
    }

    public String getNotice_no(){return notice_no;}
    public String getSubject(){return subject;}
    public String getContext(){return context;}
    public String getUpdate_dt(){return update_dt;}
}
