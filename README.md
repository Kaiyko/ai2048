# AI2048

## 基于博弈树的2048

在校期间正值学习Python相关技术，了解到了AI领域的强大，突发奇想，想到了AI玩2048，在浏览网上许多博客后选择使用博弈树完成。

基于博弈树的AI2048，在行动前对每一个可移动方向进行局面评分，选取一个评分最高的方向进行移动，使每一步都是当前局面最优解，以合成最终的2048。

项目中比较复杂的是α-β剪枝的实现，以及当前局面进行评分，需要调试出合理的系数也就是超参数，UI页面直接使用了Github上star最多的2048项目进行改造，服务器端博弈树的实现使用了较为熟悉的Java。

项目完成后合成2048成功率较高，但是合成4096成功率比较低，在继续学习机器学习相关技术后了解到了强化学习，如果结合强化学习，应该可以更加完善，后续可以考虑跟进。AI领域实在是太强大了，无论是计算机视觉，自然语言处理，还是机器学习中的预测，分类，深度学习等等，都让我惊叹数据以及知识的强大。



## 项目演示

http://119.29.137.127/2048go/



### todo：项目详解

