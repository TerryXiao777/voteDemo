package com.demo.dao;

import com.demo.bean.OptionBean;
import com.demo.tools.JDBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OptionDao {
    private JDBConnection connection = null;

    public OptionDao(){
        connection = new JDBConnection();
    }

    public List getOptions(){
        String sql="select * from tb_option order by option_order";
        List options=getList1(sql,null);
        return options;
    }
    public List getOptionsForDay(){
        String sql="select o.*,t.num from tb_option as o left join (select voter_voteoption,count(id) as num from tb_voter where day(voter_votetime)=day(curdate()) group by voter_voteoption)  t on o.id=t.voter_voteoption";
        List options =getList2(sql,null);
        return options;
    }
    public List getOptionsForMonth(){
        String sql="select o.*,t.num from tb_option as o left join (select voter_voteoption,count(id) as num from tb_voter where month(voter_votetime)=month(curdate()) group by voter_voteoption)  t on o.id=t.voter_voteoption";
        List options =getList2(sql,null);
        return options;
    }

    private List getList1(String sql,String[] params){
        List options=null;
        connection = new JDBConnection();

        try {
            ResultSet rs = connection.queryByPsStatement(sql,params);
            if(rs!=null){
                options=new ArrayList();
                while(rs.next()){
                    OptionBean single=new OptionBean();
                    single.setId(rs.getInt(1));
                    single.setOptionName(rs.getString(2));
                    single.setOptionBallot(rs.getInt(3));
                    single.setOptionOrder(rs.getInt(4));
                    options.add(single);
                }
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return options;
    }
    private List getList2(String sql,String[] params){
        List options=null;
        connection = new JDBConnection();

        try {
            ResultSet rs = connection.queryByPsStatement(sql,params);
            if(rs!=null){
                options=new ArrayList();
                while(rs.next()){
                    OptionBean single=new OptionBean();
                    single.setId(rs.getInt(1));
                    single.setOptionName(rs.getString(2));
                    single.setOptionBallot(rs.getInt(5));
                    options.add(single);
                }
                rs.close();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return options;
    }
    public int vote(int id){
        int i=-1;
        String sql="update tb_option set option_ballot=option_ballot+1 where id=?";
        String[] params={String.valueOf(id)};
        connection = new JDBConnection();
        Boolean flag = connection.updateData(sql, params);
        if(flag){
            i = 1;
        }
        return i;
    }

    public void closed(){
        connection.closeAll();
    }
}
