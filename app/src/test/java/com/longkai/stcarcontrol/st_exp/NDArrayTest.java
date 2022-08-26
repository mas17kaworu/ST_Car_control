package com.longkai.stcarcontrol.st_exp;

import org.junit.Test;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;

public class NDArrayTest {
  @Test
  public void addition_isCorrect() throws Exception {
    try (NDManager manager = NDManager.newBaseManager()) {
      NDArray a = manager.create(new int[][] { { 3, 7, 5 }, { 8, 4, 3 }, { 2, 4, 9 } });
      System.out.println("我们的数组是：");
      System.out.println(a);
      System.out.println("调用 min() 函数：");
      NDArray b = a.min(new int[] { 1 });
      a = manager.create(new float[][] { { 1, 2, 3 }, { 3, 4, 5 }, { 4, 5, 6 } });
      System.out.println("我们的数组是：");
      System.out.println(a);
      System.out.println("调用 mean() 函数：");
      b = a.mean();
      System.out.println(b);
      System.out.println("沿轴 0 调用 mean() 函数：");
      b = a.mean(new int[] { 0 });
      System.out.println(b);
      System.out.println("沿轴 1 调用 mean() 函数：");
      b = a.mean(new int[] { 1 });
      System.out.println(b);
    }
  }
}
