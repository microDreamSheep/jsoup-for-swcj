## 配置模板

```xml
<jsoup>
    <!--一个jsoup代表一个属性(name:属性名)，返回字符数组即为空-->
    <jsoup name="">
        <!--一个pa是一层解析-->
        <pa not="排除文本：分隔符:," allStep="每次跳过的步长" step="跳过步长" element="获取元素">
            你的分析文本
        </pa>
    </jsoup>
</jsoup>
```