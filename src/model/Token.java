/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Vinicius <vinicius.s.sesti@gmail.com>
 */
public class Token
{
    public String token;
    public int startChar;
    public int endChar;
    
    public Token(String token, int startChar)
    {
        this.token = token;
        this.startChar = startChar;
    }
}
