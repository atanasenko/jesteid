package com.google.code.jesteid.iso8825.test;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.code.jesteid.iso8825.TLVTag;
import com.google.code.jesteid.iso8825.TLVTag.TagClass;

public class TLVTagTest extends TestCase {
    
    @Test
    public void testPrimitiveSingleByte() {
        TLVTag t;
        
        t = new TLVTag(0x1e);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x1e);
        assertEquals(t.getTagClass(), TagClass.UNIVERSAL);
        assertFalse(t.isConstructed());
        assertEquals(t.getTag(), 0x1e);
        
        t = new TLVTag(0x40 | 0x1d);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x1d);
        assertEquals(t.getTagClass(), TagClass.APPLICATION);
        assertFalse(t.isConstructed());
        assertEquals(t.getTag(), 0x5d);
        
        t = new TLVTag(0x80 | 0x1c);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x1c);
        assertEquals(t.getTagClass(), TagClass.CONTEXT);
        assertFalse(t.isConstructed());
        assertEquals(t.getTag(), 0x9c);

        t = new TLVTag(0xc0 | 0x1b);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x1b);
        assertEquals(t.getTagClass(), TagClass.PRIVATE);
        assertFalse(t.isConstructed());
        assertEquals(t.getTag(), 0xdb);
        
    }

    @Test
    public void testConstructedSingleByte() {
        TLVTag t;
        
        t = new TLVTag(0x3e);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x1e);
        assertEquals(t.getTagClass(), TagClass.UNIVERSAL);
        assertTrue(t.isConstructed());
        assertEquals(t.getTag(), 0x3e);
        
        t = new TLVTag(0x7d);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x1d);
        assertEquals(t.getTagClass(), TagClass.APPLICATION);
        assertTrue(t.isConstructed());
        assertEquals(t.getTag(), 0x7d);
        
        t = new TLVTag(0xbc);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x1c);
        assertEquals(t.getTagClass(), TagClass.CONTEXT);
        assertTrue(t.isConstructed());
        assertEquals(t.getTag(), 0xbc);
        
        t = new TLVTag(0xfb);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x1b);
        assertEquals(t.getTagClass(), TagClass.PRIVATE);
        assertTrue(t.isConstructed());
        assertEquals(t.getTag(), 0xfb);
        
    }
    
    @Test
    public void testPrimitiveTwoByte() {
        TLVTag t;
        
        t = new TLVTag(0x1f43);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x43);
        assertEquals(t.getTagClass(), TagClass.UNIVERSAL);
        assertFalse(t.isConstructed());
        assertEquals(t.getTag(), 0x1f43);
        
        t = new TLVTag(0x5f42);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x42);
        assertEquals(t.getTagClass(), TagClass.APPLICATION);
        assertFalse(t.isConstructed());
        assertEquals(t.getTag(), 0x5f42);
        
        t = new TLVTag(0x9f41);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x41);
        assertEquals(t.getTagClass(), TagClass.CONTEXT);
        assertFalse(t.isConstructed());
        assertEquals(t.getTag(), 0x9f41);
        
        t = new TLVTag(0xdf40);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x40);
        assertEquals(t.getTagClass(), TagClass.PRIVATE);
        assertFalse(t.isConstructed());
        assertEquals(t.getTag(), 0xdf40);
    }

    @Test
    public void testConstructedThreeByte() {
        TLVTag t;
        
        t = new TLVTag(0x3fc340);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x4340);
        assertEquals(t.getTagClass(), TagClass.UNIVERSAL);
        assertTrue(t.isConstructed());
        assertEquals(t.getTag(), 0x3fc340);
        
        t = new TLVTag(0x7fc241);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x4241);
        assertEquals(t.getTagClass(), TagClass.APPLICATION);
        assertTrue(t.isConstructed());
        assertEquals(t.getTag(), 0x7fc241);
        
        t = new TLVTag(0xbfc142);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x4142);
        assertEquals(t.getTagClass(), TagClass.CONTEXT);
        assertTrue(t.isConstructed());
        assertEquals(t.getTag(), 0xbfc142);
        
        t = new TLVTag(0xffc043);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x4043);
        assertEquals(t.getTagClass(), TagClass.PRIVATE);
        assertTrue(t.isConstructed());
        assertEquals(t.getTag(), 0xffc043);
    }
    
    @Test
    public void testFourByte() {
        TLVTag t;
        
        t = new TLVTag(0x3fc3c060);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x434060);
        assertEquals(t.getTag(), 0x3fc3c060);
        
        t = new TLVTag(0x3fc3b06c);
        System.out.println(t.toDebugString());
        assertEquals(t.getNumber(), 0x43306c);
        assertEquals(t.getTag(), 0x3fc3b06c);
        
    }
    
}
