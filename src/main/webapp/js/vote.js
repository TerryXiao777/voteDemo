var mark1="off";
var mark2="off";

function checkvote(){
    var movies=document.getElementsByName("movie");
    var i=0;
    for(i=0;i<movies.length;i++){
        if(movies[i].checked){
            waitclick();
            voteform.voteb.disabled=true;
            voteform.submit();
            break;
        }
    }
    if(i==movies.length){
        message.innerHTML="Please select one";
    }

}

function waitload(){
    parent.wait.style.display='none';
    parent.result.style.display='';
}
function waitclick(){
    wait.style.display='';
    result.style.display='none';
}
//在showresult.jsp页面中调用的脚本，用于实现图片显示区域的缩放
function size(){
    //获取父页面(toresult.jsp)中id属性值为“resultpic”的元素（这里为iframe框架）
    let tag1=parent.document.getElementById("resultpic");
    if(mark1=="off"){
        mark1="on";
        //将tag1元素的高度设置为showresult.jsp页面的高度，实现放大效果
        tag1.height=document.body.scrollHeight;
    }
    else{
        mark1="off";
        //将tag1元素的高度设置为指定值，实现缩小效果
        tag1.height=350;
    }
    //获取父页面的父页面(main.jsp)中id属性值为“resultpage”的元素（这里为iframe框架）
    let tag2=parent.parent.document.getElementById("resultpage");
    if(mark2=="off"){
        mark2="on";
        //将tag2元素的高度设置为showresult.jsp的父页面toresult.jsp的高度，实现放大效果
        tag2.height=parent.document.body.scrollHeight;
    }
    else{
        mark2="off";
        //将tag2元素的高度设置为指定值，实现缩小效果
        tag2.height=450;
    }
}