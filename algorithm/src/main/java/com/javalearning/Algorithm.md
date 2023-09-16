# 算法模块 algorithm
- leetcode 面向面试的算法，以LeetCode为主
  - components  算法题需要用到的公共组件类
  - utils 调试算法方便而写的工具方法
  - _0xXXX 算法题分类
- freeresearch 自由研究
  - SortingAlgorithm 面试时用得着的排序算法
  - CodingInterviews 如果有时间会刷的剑指offer算法题，看看究竟是啥


# labudadong系列算法文章

gitbook链接   [labuladong的算法小抄](https://labuladong.gitbook.io/algo/)

## 动态规划入门

已读文章，科学排序

[动态规划详解（修订版）](https://mp.weixin.qq.com/s/Cw39C9MY9Wr2JlcvBQZMcA)

[从最长递增子序列学会如何推状态转移方程](https://mp.weixin.qq.com/s/7QFapCuvi-2nkh6gREcR9g)

[动态规划套路：最大子数组和](https://mp.weixin.qq.com/s/nrULqCsRsrPKi3Y-nUfnqg)

[动态规划之正则表达式](https://mp.weixin.qq.com/s?__biz=MzAxODQxMDM0Mw==&mid=2247484513&idx=1&sn=e5fc3cce76c1b916195e1793122c28b8&chksm=9bd7fa69aca0737fe704ea5c6da28f47b9e3f0961df2eb40ef93a7d507ace8def1a18d013515&scene=158#rd) 
```
正则表达式问题提供了快速看出重叠子问题的一种思路，便于确定是否值得使用备忘录
```
[LeetCode 股票问题的一种通用解法](https://mp.weixin.qq.com/s/TrN7mMdLEPCmT5mOXzgP5A)
```
重叠子问题的特征：一个子数组切片可以通过多条递归路径得到
本文重点研究递归这种通用解法，不是最优的，在测试用例特别大时OOM。改进算法见下一篇
```
[团灭 LeetCode 股票买卖问题](https://mp.weixin.qq.com/s/lQEj_K1lUY83QtIzqTikGA)
```
动态规划转移方程做点改动可以通杀系列股票问题
```
[经典动态规划：0-1 背包问题](https://mp.weixin.qq.com/s/RXfnhSpVBmVneQjDSUSAVQ)
[经典动态规划：0-1 背包问题的变体 - 子集背包问题](https://mp.weixin.qq.com/s/OzdkF30p5BHelCi6inAnNg)
```
二维动态规划数组如何降维 - 注意ij内外层遍历和sum倒序遍历防止数字重复使用的问题
```
[经典动态规划：完全背包问题](https://mp.weixin.qq.com/s/zGJZpsGVMlk-Vc2PEY4RPw)

[最长公共子序列](https://mp.weixin.qq.com/s?__biz=MzAxODQxMDM0Mw==&mid=2247484486&idx=1&sn=0bdcb94c6390307ea32427757ec0072c)

[最小编辑距离](https://mp.weixin.qq.com/s/uWzSvWWI-bWAV3UANBtyOw)

[KMP算法](https://mp.weixin.qq.com/s/r9pbkMyFyMAvmkf4QnL-1g)

[动态规划之子序列问题解题模板-最长回文子序列](https://mp.weixin.qq.com/s?src=11&timestamp=1602920263&ver=2649&signature=NPL-Jl6Qt1e3P702UFwLsruBlGV36G98dlfhsAY3XDhzZFCbyjEuolTDUxT2ztz54GU8SzoWmkl0h2YdEkc9aC-hk4NLF9Ei0hfyeC3JlPTI1Hvh71buu*KOVZKG5wzd&new=1) #516
```
复述时研究下马拉车算法
```
[动态规划之博弈问题](https://labuladong.gitbook.io/algo/dong-tai-gui-hua-xi-lie/dong-tai-gui-hua-zhi-bo-yi-wen-ti)
[动态规划之四键键盘](https://labuladong.gitbook.io/algo/dong-tai-gui-hua-xi-lie/dong-tai-gui-hua-zhi-si-jian-jian-pan)

[打家劫舍问题](https://mp.weixin.qq.com/s?__biz=MzAxODQxMDM0Mw==&mid=2247484800&idx=1&sn=1016975b9e8df0b8f6df996a5fded0af&chksm=9bd7fb88aca0729eb2d450cca8111abd8f861236b04125ce556171cb520e298ddec4d90823b3&scene=21#wechat_redirect)


- 特殊动态规划：贪心算法
[贪心算法之区间调度问题](https://labuladong.gitbook.io/algo/dong-tai-gui-hua-xi-lie/tan-xin-suan-fa-zhi-qu-jian-tiao-du-wen-ti)
[经典动态规划之最小数量的箭射气球问题](https://mp.weixin.qq.com/s?src=11&timestamp=1602920369&ver=2649&signature=NPL-Jl6Qt1e3P702UFwLsruBlGV36G98dlfhsAY3XDgC9zO2RShjaiqgzY6uW*nuiBaZOiRMZWu5vh9uijlKkq3puXs8fgOBoAPwt3uZIQphGV1fWsER8v3UJgx7ATYi&new=1)

- 复杂动态规划
[高楼扔鸡蛋](https://mp.weixin.qq.com/s?src=11&timestamp=1602920440&ver=2649&signature=NPL-Jl6Qt1e3P702UFwLsruBlGV36G98dlfhsAY3XDiZnpWohskQ-72M5Hu7npaKZaWGi6Pw3Novh*SFrz4aYToeUcstSxu8Smez5KD5eGkCAFREtFJSATyvnK7VhOaV&new=1)
[经典动态规划之高楼扔鸡蛋进阶版](https://mp.weixin.qq.com/s?src=11&timestamp=1602920440&ver=2649&signature=NPL-Jl6Qt1e3P702UFwLsruBlGV36G98dlfhsAY3XDiAanOnNIpwb*A2O74UrcND-rOjiANZI68K1I5ZJezh9pwoZTn0uyjl4ya-JFJqE3Nj4pv-cGqGV7ju67*4S1br&new=1)
[戳气球问题]()


[我作了首诗，保你闭着眼睛也能写对二分查找](https://mp.weixin.qq.com/s?__biz=MzAxODQxMDM0Mw==&mid=2247485044&idx=1&sn=e6b95782141c17abe206bfe2323a4226&chksm=9bd7f87caca0716aa5add0ddddce0bfe06f1f878aafb35113644ebf0cf0bfe51659da1c1b733&scene=21#wechat_redirect)

### 数组

[去除重复字母]()
[单调栈的使用]()
[有序数组去重]()

## 数据结构

[水塘抽样算法](https://labuladong.gitbook.io/algo/gao-pin-mian-shi-xi-lie/shui-tang-chou-yang)
[递归反转链表的一部分]()
[如何k个一组反转链表]()
[如何判断回文链表]()
[双指针技巧汇总]()

[二叉树最近公共祖先]()
[手把手带你刷二叉树（第一期）]()
[手把手带你手刷二叉树（第二期）]()
[手把手带你手刷二叉树（第三期）]()
刷二叉搜索树（第一期）
[]()
[]()
[]()
[]()
[]()
[]()


手把手实现LRU算法
手把手实现LFU算法
DFS与BFS
如何高效解决接雨水问题



东哥吃葡萄时竟然吃出一道算法题！
回溯算法最佳实践：括号生成
FloodFill算法详解及应用
如何调度考生的座位
二分搜索只能用来查找元素吗？
如何用算法高效寻找素数？
《动态规划详解（修订版）》和《回溯算法详解（修订版）》

https://com.javalearning.leetcode-cn.com/problems/remove-duplicate-letters/solution/yi-zhao-chi-bian-li-kou-si-dao-ti-ma-ma-zai-ye-b-4/

## 待定