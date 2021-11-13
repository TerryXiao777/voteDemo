package com.demo.dao;

import com.demo.tools.JDBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class VoterDao {
    private JDBConnection connection = null;

    public VoterDao(){
        connection = new JDBConnection();
    }

    public Date getLastVoteTime(long ip) throws SQLException {
        Date time=null;
        String sql="select max(voter_votetime) from tb_voter where voter_ip=?";
        String[] params={String.valueOf(ip)};
        connection = new JDBConnection();

        ResultSet rs=connection.queryByPsStatement(sql,params);
        if(rs!=null && rs.next()){
            time=rs.getTimestamp(1);
            rs.close();
        }
        connection.closeAll();
        return time;
    }
    public void saveVoteTime(String[] params){
        String sql="insert into tb_voter values(?,?,?)";
        connection = new JDBConnection();
        connection.updateData(sql,params);
        connection.closeAll();
    }
}
