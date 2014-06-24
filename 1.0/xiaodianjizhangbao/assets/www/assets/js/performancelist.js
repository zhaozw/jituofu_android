/**
 * Created by apple on 12/3/13.
 */
define(function (require, exports, module){
    var $ = require("zepto.min.js");
    var IO = require("io.js");
    var Utils = require("utils.js");
    var Rent = require("rent.js");

    function initPage(){
        var rents = {};

        function setDate(){
            var today = new Date();
            var y = today.getFullYear();
            var m = Utils.to2Num(today.getMonth()+1);
            var d = Utils.to2Num(today.getDate());
            var startNode = $('#J-dateStart');
            var endNode = $('#J-dateEnd');
            var date = y+"-"+m+"-"+d;
            startNode.html(date);
            endNode.html(date);
            updateTitle(m+"."+d, m+"."+d);

            startNode.unbind().bind('click', function (e){
                e.preventDefault();
                var currentField = $(this);
                var myNewDate = currentField.html();
                showDate(myNewDate, currentField);
                currentField.blur();
            });
            endNode.unbind().bind('click', function (e){
                e.preventDefault();
                var currentField = $(this);
                var myNewDate = currentField.html();
                showDate(myNewDate, currentField);
                currentField.blur();
            })
        }
        function showDate(extraDate, ele){
            window.plugins.datePicker.show({
                date : new Date(extraDate.replace(/\-/g, '/')),
                mode : 'date', // date or time or blank for both
                allowOldDates : true
            }, function(returnDate) {
                if(returnDate){
                    var nd = new Date(returnDate);
                    var y = nd.getFullYear();
                    var m = Utils.to2Num(nd.getMonth()+1);
                    var d = Utils.to2Num(nd.getDate());
                    var date = y+"-"+m+"-"+d;
                    ele.html(date);
                }
            });
        }
        function updateTitle(start, end){
            $('.header .title').html(start+"-"+end+" 报表");
        }
        function getStartTIme(){
            var val = $.trim($('#J-dateStart').html());
            if(!val){
                return null;
            }
            return val.replace(/\//g, '-');
        }

        function getEndTIme(){
            var val = $.trim($('#J-dateEnd').html());
            if(!val){
                return null;
            }
            return val.replace(/\//g, '-');
        }
        function queryPerf(){
            if(!getStartTIme()){
                return Utils.alert('请选择开始时间');
            }else if(!getEndTIme()){
                return Utils.alert('请选择结束时间');
            }
            var start = getStartTIme()['split']('-');
            var end = getEndTIme()['split']('-');
            if(start[0] !== end[0]){
                return Utils.alert("暂时不支持跨年度查询");
            }else if((start[0] === end[0]) && (start[1] > end[1])){
                return Utils.alert('结束时间小于开始时间');
            }else if((start[0] === end[0]) && (start[1] === end[1]) && (start[2] > end[2])){
                return Utils.alert('结束时间小于开始时间');
            }

            queryBtn.unbind();
            $('.tip').show().css({
                color: "inherit"
            }).html("查询中...");
            updateTitle(start[1]+"."+start[2],end[1]+"."+end[2]);
            var perf = require("performance.js");
            perf.io({
                range: true,
                data: "action=query&start="+getStartTIme()+'&end='+getEndTIme(),
                on: {
                    success: function (data){
                        if(data.bizCode !== 1){
                            $('.tip').show().css({
                                color: "#f50"
                            }).html(data.memo);
                            queryBtn.bind('click', queryPerf);
                            return;
                        }else if(data.data.products && data.data.products.length === 0){
                            $('.tip').show().css({
                                color: "#f50"
                            }).html("没有销售记录");
                            data.yye = 0;
                            data.cb = 0;
                        }else{
                            $('.tip').hide();
                        }
                        queryBtn.bind('click', queryPerf);
                        Rent.getRange(getStartTIme(), getEndTIme(), function (rentData){
                            if (data.data.products && data.data.products.length === 0) {
                                data.zj = 0;
                            }
                            if(rentData.data && rentData.data.rents && rentData.data.rents.length >= 1){
                                $.each(rentData.data.rents, function (i, rent){
                                    data.zj += rent.price*1;
                                    rents[rent.date.split(' ')[0]] = rent.price*1;
                                });
                            }
                            data.lr = data.yye - data.cb - data.zj;
                            updatePage(data);
                        });
                    },
                    error: function (){
                        $('.tip').show().css({
                            color: "#f50"
                        }).html("请求发生异常，请重试");
                        queryBtn.bind('click', queryPerf);
                    }
                }
            });
        }

        function updatePage(data){
            $('.perf').show();
            $('.charts').empty().show();
            $('.types').empty().show();

            var zj = data.zj.toFixed(2).split('.');
            var lr = data.lr.toFixed(2).split('.');
            var cb = data.cb.toFixed(2).split('.');
            var yye = data.yye.toFixed(2).split('.');
            $('#J-yye').html(yye[0]+"<small>."+yye[1]+"</small>元");
            $('#J-cb').html(cb[0]+"<small>."+cb[1]+"</small>元");
            $('#J-lr').html(lr[0]+"<small>."+lr[1]+"</small>元");
            $('#J-zj').html(zj[0]+"<small>."+zj[1]+"</small>元");


            var typesHtml = '';
            var countedOrderId = [];
            for(i in data.types){
                var count = 0;
                if(data.types[i].length >= 1){
                    var maxFloat = [];
                    for(var j = 0;j < data.types[i]['length']; j++){
                        var countSplit = (data.types[i][j]['p_count']+'').split('.');
                        if(countSplit && countSplit[1]){
                            maxFloat.push((countSplit[1]+'')['length']);
                        }
                        if(countedOrderId.indexOf(data.types[i][j]['order_id']) === -1){
                            count += data.types[i][j]['p_count']*1;
                            countedOrderId.push(data.types[i][j]['order_id']);
                        }
                    }
                    if(maxFloat.length >= 1){
                        count = (count*1).toFixed(Math.max.apply(null, maxFloat));
                    }
                    typesHtml += '<p>'+i+': '+count+' 个</p>';
                }
            }
            $('.types').html(typesHtml);

            var dateTypeHtml = '';
            var lrs = [];
            for(k in data.dateType){
                if(!rents[k]){
                    rents[k] = 0;
                }
                data.dateType[k]['lr'] -= rents[k];
                lrs.push(data.dateType[k]['lr']);
            }
            var dateKeys = Object.keys(data.dateType).sort();
            $.each(dateKeys, function (i, date){
                if(data.dateType[date]){
                    var dateSplit = date.split('-');
                    var lr = data.dateType[date]['lr'].toFixed(2).split('.');
                    var w = getWidth(data.dateType[date]['lr']);
                    var style = '';
                    var lrstatus = "";

                    if(data.dateType[date]['lr'] < 0){
                        lrstatus = "亏本";
                    }else if(data.dateType[date]['lr'] == 0){
                        lrstatus = "保本";
                    }else if(data.dateType[date]['lr'] > 0){
                        lrstatus = "纯利";
                    }
                    if(data.dateType[date]['lr'] <= 0){
                        style = "border-left: 5px solid red;";
                        w = 0;
                    }
                    dateTypeHtml += '<li class="chart"><a href="productlist.html?date='+(data.dateType[date][0]['date'].split(' ')[0])+'" class="trigger J-hideTabBar" target="_blank" style="'+style+'">'+
                        '<div class="back"></div>'+
                        '  <p>'+(dateSplit[1]+"-"+dateSplit[2])+'</p>'+
                        '  <p>'+lrstatus+'：'+lr[0]+'<small>.'+lr[1]+'</small> 元</p>'+
                        '<div class="front" style="width: '+w+'%;"></div>'+
                        '</a></li>';
                }
            });
            $('.charts').html(dateTypeHtml);
            function getWidth(w){
                var maxlr = Math.max.apply( Math, lrs);
                var width = (w / maxlr) * 100;
                return Math.abs(width);
            }
        }

        var queryBtn = $('#J-queryBtn');

        queryBtn.bind('click', queryPerf);
        setDate();
    }

    initPage();
});
