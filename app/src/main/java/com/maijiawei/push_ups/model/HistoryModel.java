package com.maijiawei.push_ups.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by Administrator on 2016/4/30.
 */
@Table(name = "History")
public class HistoryModel extends Model {

    @Column(name = "time")
    public int time;
    @Column(name = "val")
    public String val;
    @Column(name = "createTime")
    public long createTime;

    public static List<HistoryModel> getAll() {
        return new Select()
                .from(HistoryModel.class)
                .orderBy("createTime desc")
                .execute();
    }
}
