package dao;

import org.junit.Test;

import static org.junit.Assert.*;

public class DictionaryTagDAOTest {
    @Test
    public void t1(){
        DictionaryTagDAO d=new DictionaryTagDAO();
        d.query("2");
    }

}