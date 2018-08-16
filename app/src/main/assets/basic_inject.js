/** 注：该JS文件用于存放常用函数，功用相关的函数放在Java文件中注入*/


//##############################################################################################################
//设置账号密码
function tblmShopList(){
    var boxContent = document.getElementsByClassName("box-content");
    localMethod.JI_LOG(boxContent.length);


}


function shangjiaNow(){
    var checkboxs = document.getElementsByClassName("checkbox-wrap");
    var comfirs = document.getElementsByClassName("blue");
    var radios = checkboxs[17].getElementsByTagName("input");
    radios[0].click();
    localMethod.JI_LOG(comfirs.length);
//    for(var j=0;j<comfirs.length;j++){
//        localMethod.JI_LOG(comfirs[j].innerText);
//    }
    comfirs[1].click();

}

function checkAllOnSales(){
    var salesNum = document.getElementsByClassName("page-nav inline-block");
    var mInnerText = salesNum[0].innerText;
    var pageNums = mInnerText.split("...");
    var nums = pageNums[1].split("下一页");
    localMethod.JI_LOG(nums[0]);
    localMethod.foreachShangJia(nums[0]);
    var input = document.getElementsByTagName("input");
    input[112].value = nums[0];
    input[113].click();
}

function selectXiajia(shangjiaRecord){
    var selectors = document.getElementsByClassName("selector");
    var itemCates = document.getElementsByClassName("J_QRCode");
    localMethod.JI_LOG(shangjiaRecord+"");
    var xiajiaStr = "";
    for(var j=0;j<selectors.length;j++){
        var itemids = selectors[j].getAttribute("itemids");
        var icat = itemCates[j].getAttribute("data-param").split("&cid=")[1].split("&title=")[0];
        if(xiajiaStr==""){
            xiajiaStr = itemids+"@@@"+icat;
        } else {
            xiajiaStr = xiajiaStr + "###" +itemids+"@@@"+icat;
        }
    }
    localMethod.xiaJiaRecord(xiajiaStr+"");
}

function xiajia(){
    var kbutton = document.getElementsByClassName("kbutton");
    localMethod.JI_LOG("click!!!!!!"+kbutton.length);
    kbutton[6].click();
    localMethod.JI_LOG("click!!!!!!");
}

function goIndexPage(len,index){
    localMethod.JI_LOG("len"+len);
    localMethod.JI_LOG("@@@@"+index);
    var input = document.getElementsByTagName("input");
    input[112].value = index;
    input[113].click();


    if(len==index){
        localMethod.xiajiaClick();
    }
}


//标题组合
function titleCombination(){

    var as = document.getElementsByTagName("a");
    findForClick(as,"关联修饰词");
    for(var j=0;j<5;j++){
        setTimeout(function(){
        getTableTitleData();
            var as = document.getElementsByTagName("a");
            findForClick(as,"下一页 >");
        },500*(j+1));
    }

    setTimeout(function(){
        localMethod.getTitleResult();
    },3000);
//    localMethod.getTitleResult();

}

//标题组合
function relativeTitle(){

    var as = document.getElementsByTagName("a");
    findForClick(as,"关联热词");
    for(var j=0;j<5;j++){
        setTimeout(function(){
        getTableTitleData();
            var as = document.getElementsByTagName("a");
            findForClick(as,"下一页 >");
        },500*(j+1));
    }

       setTimeout(function(){
            titleCombination();
        },3000);

//    localMethod.getTitleResult();

}

//跳到市场
function goSearchClick(){

    var as = document.getElementsByTagName("a");
//    for (var i = 0; i < as.length; i++) {
//        localMethod.JI_LOG(as[i].innerHTML+"~~~~"+i);
//    }
    as[18].click();

    setTimeout(function(){
                var as2 = document.getElementsByTagName("a");
                as2[27].click();
                localMethod.JI_LOG("as2~~~~click");
    },3000);

}


function check(){
    var as = document.getElementsByTagName("a");
    var checkboxs = document.getElementsByClassName("checkbox undefined undefined");
    var options = document.getElementsByClassName("option");
        localMethod.JI_LOG(as.length);
        localMethod.JI_LOG(checkboxs.length);
        localMethod.JI_LOG(options.length);
    var text = "--------as--------"+ "\n";
    for(var i=0;i<as.length;i++){
        text = text + i + "\n"+",innerText:"+as[i].innerText+
//        ",innerHTML:"+as[i].innerHTML+
        ",value:"+as[i].value + ",as:"+as[i]+"\n";
    }
        localMethod.JI_LOG(text);
    text = "--------checkboxs--------"+ "\n";
    for(var i=0;i<checkboxs.length;i++){
        text = text + i+ "\n" +"checkboxs:"+checkboxs[i]+",innerText:"+checkboxs[i].innerText+
//        ",innerHTML:"+checkboxs[i].innerHTML+
        ",value:"+checkboxs[i].value+"\n";
    }
            localMethod.JI_LOG(text);
    text = "--------options--------"+ "\n";
    for(var i=0;i<options.length;i++){
        text = text + i+ "\n" + "options:"+options[i]+",innerText:"+options[i].innerText+",innerHTML:"+options[i].innerHTML+",value:"+options[i].value+"\n";
    }
    localMethod.JI_LOG(text);
}

function goSearchWord(){
    var as = document.getElementsByTagName("a");
    as[26].click();
}


function setSearchWord(shopword){

    var as = document.getElementsByTagName("a");
    var searchWord = document.getElementsByClassName("main-search-input");
    searchWord[0].focus();
    localMethod.showKeyboard();
    searchWord[0].value = shopword;
    setTimeout(function(){
            findForClick(as,"搜索");
            var as2 = document.getElementsByTagName("a");
            findForClick(as2,"相关搜索词");
            setTimeout(function(){localMethod.getTargetIndex();},2000);

    },2000);
}

//根据搜索词点击
function findForClick(as,word){
    for(var i=0;i<as.length;i++){
        if(as[i].innerText==word){
           as[i].click();
        }
    }
}

//竞争力、热词获取排序
function goGetChecked(){

    for(var j=0;j<5;j++){
        setTimeout(function(){
            getTableData();
            var as = document.getElementsByTagName("a");
            findForClick(as,"下一页 >");
        },500*(j+1));
    }

    setTimeout(function(){
        localMethod.getHotShopResult();
    },3000);
}


function getTableTitleData(){
    var table = document.getElementsByClassName("table-ng table-ng-basic related-word-table")[0];
    for(var i=0;i<table.rows.length;i++){
        var child = table.getElementsByTagName("tr")[i];
        var text = child.children[0].innerText;
        var text1 = child.children[1].innerText;
        if(i>0){
            text1 = text1.replace("-","0");

            text1 = text1.replace(",","").replace(",","");
//            localMethod.JI_LOG(text+"~~~~~~~~~~");
//            localMethod.JI_LOG(text1+"!!!!!!!!");
            if(text1>700){
                localMethod.titleResult(text,text1);
            }


        }
    }
}

function getTableData(){
    var table = document.getElementsByClassName("table-ng table-ng-basic related-word-table")[0];
    for(var i=0;i<table.rows.length;i++){
        var child = table.getElementsByTagName("tr")[i];
        var text = child.children[0].innerText;
        var text1 = child.children[1].innerText;
        var text2 = child.children[2].innerText;
        var text3 = child.children[3].innerText;
        var text4 = child.children[4].innerText;
        if(i>0){
            var djl = text2.replace("%","");
            djl= djl/100;
            var zhl = text4.replace("%","");
            zhl= zhl/100;
            text1 = text1.replace("-","0");
            text2 = text2.replace("-","0");
            text3 = text3.replace("-","0");
            text4 = text4.replace("-","0");

            text1 = text1.replace(",","").replace(",","");
            text3 = text3.replace(",","").replace(",","");
//            localMethod.JI_LOG(text1+"!!!!!!!!");
//            localMethod.JI_LOG(text3+"!!!!!!!!");
            if(text3!=("0")&&text1>2000){
                var jzl = accDiv(accMul(accMul(text1,djl),zhl),text3);
                var rc = accDiv(text1,text3);
//                localMethod.JI_LOG(jzl+"~~~~~");
//                localMethod.JI_LOG(rc+"~~~~~");
                localMethod.shopResult(text,jzl,rc);
            }


        }
    }
}

//乘法
function accMul(arg1,arg2){
 try{
    var m=0,s1=arg1.toString(),s2=arg2.toString();
    try{
        m+=s1.split(".")[1].length
        }catch(e){
        }
    try{
        m+=s2.split(".")[1].length
        }catch(e){
        }
    return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);
 }catch(e){
    return 0;
 }
}

//除法
 function accDiv(arg1,arg2){
 try{
    var t1=0,t2=0,r1,r2;
    try{
        t1=arg1.toString().split(".")[1].length
        }catch(e){}
    try{
        t2=arg2.toString().split(".")[1].length
        }catch(e){}
    with(Math){
        r1=Number(arg1.toString().replace(".",""));
        r2=Number(arg2.toString().replace(".",""));
        return (r1/r2)*pow(10,t2-t1);
    }
 }catch(e){
    return 0;
 }
}

//指标选择
function operaSearch(){

    var checkboxs = document.getElementsByClassName("checkbox undefined undefined");
    var optionsClick = document.getElementsByClassName("option");

    for(var i=0;i<checkboxs.length;i++){
        if(checkboxs[i].innerText=="搜索人数占比"||checkboxs[i].innerText=="搜索热度"||checkboxs[i].innerText=="商城点击占比"
        ||checkboxs[i].innerText=="直通车参考价"||checkboxs[i].innerText=="支付转化率"){
           optionsClick[i].click();
        }
    }
    goGetChecked();
}

function foreachThings(options,i){

        setTimeout(function(){
//           options[i].click();
           localMethod.JI_LOG(options[i].innerHTML+"check_option~~~~"+i);

        },500*(i+1));

}


/** No.1 模拟点击事件############################################################################################*/
//模拟点击事件
function doClickByRI(resId,time) {
 var btn = document.getElementById(resId);
 if(null!=btn){
    setTimeout(function(){
        btn.click();
    },time*1000);
    }
}

function doClickByTag(){
  var itemli = document.getElementsByTagName("li");
  localMethod.JI_showToast("length："+itemli.length);

}
function doComfir(){

    setTimeout(function(){

  var btn = document.getElementsByClassName("layui-layer-btn0");
    localMethod.JI_showToast("btn:"+btn.length);
    btn[0].click();
        },3000);

}


function selectNumRange(position,amount){
  var itema = document.getElementById('framePage').contentWindow.document.getElementsByTagName('input');
  var commitBtn = document.getElementById('framePage').contentWindow.document.getElementById('openBetWinBtn2');


  localMethod.JI_showToast("itema:"+itema.length);
    itema[position].click();
    itema[position].value = amount;
    setTimeout(function(){
        commitBtn.click()
    },2000);

//  localMethod.JI_LOG(btn.className);
    setTimeout(function(){
        btn.click()
    },3000);

}


function doClickByCN(className,time) {
  var itemli = document.getElementsByTagName("li");
  localMethod.JI_showToast("length："+itemli.length);

  var btn = document.getElementsByClassName(className)[0];
  if(null!=btn){
    setTimeout(function(){
        btn.click();
    },time*1000);
    }
}

//模拟触摸事件
function doTapByRI(resId,index) {
   if(null==index){index=0;}
   $("#"+resId).eq(index).trigger("tap");
}

function doTapByCN(className,index) {
  if(null==index){index=0;}
  $("."+className).eq(index).trigger("tap")
}

//根据父控件查找子控件再触摸
function doTapByParentCN(parentCN,className,index) {
  if(null==index){index=0;}
  $("."+parentCN).children("."+className) .eq(index).trigger("tap");
}

function doTapForScanGoods(parentCN,index) {
   if(null==index){index=0;}
   $("."+parentCN).eq(index).children(".p").children("a").trigger("tap");
}


/** No.2 输入文本信息至输入框中############################################################################################*/
function doInputByRI(resId,context,time) {
   var text = document.getElementById(resId);
    setTimeout(function(){
        text.value = context;
    },time*1000);
}

function doInputByCN(className,context,time) {
    var text = document.getElementsByClassName(className)[0];
    setTimeout(function(){
        text.value = context;
    },time*1000);
}


/** No.3 获取控件的文本信息###########################################################################################*/
function doGetTextByRI(resId) {
    var text = document.getElementById(resId);
    return text.value;
}

function doGetTextByCN(className) {
    var text = document.getElementsByClassName(className)[0];
    return text.value;
}

function doGetTextByCNByInner(className) {
    var text = document.getElementsByClassName(className)[0];
    return text.innerHTML;
}



