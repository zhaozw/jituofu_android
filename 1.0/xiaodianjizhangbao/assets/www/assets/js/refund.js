/**
 * Created with JetBrains PhpStorm.
 * User: praise
 * Date: 11/6/13
 * Time: 4:33 PM
 * To change this template use File | Settings | File Templates.
 */
define(function (require, exports, module){
    var $ = require('zepto.min.js');
    var Pop = require('pop.js');
    var IO = require("io.js");
    var Utils = require("utils.js");

    var html = '';
    var pop = null;
    pop = new Pop({
        bd: html,
        bindUI: function (){

        },
        styles: {
            width: 230
        }
    });
    pop.render();

    function submitRefund(data, callback){
        var detailNodes = pop.bd.find('.detailsBox .detail');
        var details = {};//最终传入服务端的details值
        var checkeds = 0;//已勾选的销售记录
        var refundDetail = [];//销售记录的退货存储器
        var confirm = null;

        var selectCountMax = 0;//有多少下拉框的值选择中的是销售数量的最大值
        var selectCount = 0;//弹框里有多少带有下拉框的销售记录
        $.each(detailNodes, function (i, n){
            var select = $(n).find('.J-counter');

            if($(n).find('input[type=checkbox]').get(0).checked && !select.get(0)){
                var _del = $(n).attr('data-detail').split('*');
                details[(i+1)] = [_del[1]+','+_del[1]];
                checkeds++;
                refundDetail.push($(n).attr('data-detail'))
            }else if($(n).find('input[type=checkbox]').get(0).checked//如果勾选，并且下拉列表的值小于当前销售数量的最大值
                && select.get(0)
                ){
                refundDetail.push($(n).attr('data-detail'));//存储这条销售记录
                //details.push(select.attr('data-price')+'*'+(select.attr('data-max')-select.val()))//存储剩余的销售记录到服务端
                details[(i+1)] = [select.attr('data-max')+','+select.val()];
                checkeds++;//记录当前勾选状态
            }

            if(select.get(0)){
                selectCount++;
            }
            if(select.get(0) && select.val() === select.attr('data-max')){
                selectCountMax++;
            }
        });
        if(checkeds === 0){
            return Utils.alert('请勾选一个退货商品');
        }
        if(checkeds === detailNodes.length && selectCountMax === selectCount){
            Utils.confirm("系统将直接删除这条销售记录", function (which){
                    requestRefundIO();
            });
            details = 'null';
        }else{
            Utils.confirm(data.name+" 退货："+refundDetail.join('和 ').replace(/\*/g, ' x '), function (which){
                    requestRefundIO();
            });
        }
        function requestRefundIO(){
            new IO({
                url: 'refund.php',
                data: 'action=refund&id='+data.id+"&details="+(details === 'null' ? details : JSON.stringify(details)),
                on: {
                    start: function (){
                        Utils.loading.show("正在退货...");
                    },
                    success: function (data){
                        if(data.data.sold || data.data.deleted){
                            setTimeout(function (){
                                Utils.loading.hide();
                            }, 1500);
                        }
                        if(data.bizCode !== 1){
                            setTimeout(function (){
                                Utils.loading.hide();
                            }, 1500);
                        }
                        if(data.bizCode === 1){
                            Utils.loading.hide();
                        }
                        callback.call(callback, data);
                    },
                    error: function (){
                        setTimeout(function (){
                            Utils.loading.hide();
                        }, 1500);
                    }
                }
            }).send();
        }
    }

    function bindUItoPop(){
        pop.bd.find('.J-counter').unbind().bind('change', function (){
            var max = $(this).attr('data-max');//
            var val = $(this).val();
            var price = $(this).attr('data-price');
            //如果选择了这条销售记录，就改变data-detail的值
            if($(this).parent().find('input[type=checkbox]').get(0).checked){
                $(this).parent().attr('data-detail', price+'*'+val);
            }
        });

        pop.bd.find('.detail input[type=checkbox]').unbind().bind('click', function (e){
            var select = $(this).parent().find('.J-counter');
            if(select.get(0)){
                //如果勾选上，也要改变data-detail的值
                if($(this).get(0).checked){
                    $(this).parent().attr('data-detail', select.attr('data-price')+"*"+select.val());
                }else{
                    //如果不勾选，也恢复data-detail
                    $(this).parent().attr('data-detail', select.attr('data-price')+"*"+select.attr('data-max'));
                }
            }
        });
    }

    return {
        showPanel: function (data, callback){
            pop.show().hd.html(data.name+" 的退货");
            var detailHtml = '<div class="detailsBox">';
            $.each(data.details, function (i, detail){
                detail = detail.split('*');
                var uPrice = detail[0];//销售价格
                var uCount = detail[1]*1;//销售数据
                var price = (uPrice*1).toFixed(2)+'元';//拼装后的销售价格

                var uCountSplit = (uCount+'').split('.');
                if(uCountSplit && uCountSplit[1]){
                    var denominator = 1;
                    if(uCountSplit && uCountSplit[1]){
                        denominator = Math.pow(10, uCountSplit[1].length);
                    }
                    if(uCount >= (1/denominator)*2){
                        var count = '<select class="J-counter" data-max="'+uCount+'" data-price="'+uPrice*1+'">';
                        for (
                            var k = (1/denominator);
                            k < uCount + (1/denominator);
                            (uCountSplit[1] ? k=(k*1+1/denominator).toFixed(uCountSplit[1].length) : k++)
                            ) {

                            var selected = '';
                            if (k*1 === uCount) {
                                selected = "selected";
                            }
                            count += '<option value="' + k + '" ' + selected + '>' + k + '个</option>';
                        }
                        count += '</select>';
                    }else{
                        var count = uCount;
                    }
                }else{
                    if(uCount < 2){
                        var count = uCount;
                    }else{//如果销售数量大于1,就渲染一个下拉框
                        count = '<select class="J-counter" data-max="'+uCount+'" data-price="'+uPrice*1+'">';
                        for(var j= 1; j<=uCount; j++){
                            var selected = '';
                            if(j === uCount){
                                selected = 'selected';
                            }
                            count += '<option value="'+j+'" selected>'+j+'</option>';
                        }
                        count += '</select>';
                    }
                }

                count += ' 个';
                var checkbox = '<input type="checkbox" />';
                detailHtml += '<div class="detail" data-detail="'+uPrice+'*'+uCount+'">'+price+' x '+count+checkbox+'</div>';
            });
            detailHtml + '</div>';
            pop.bd.html(detailHtml);
            bindUItoPop();
            pop.ft.find('.btn1').unbind().bind('click', function (e){
                e.preventDefault();
                submitRefund(data, callback);
            });
            pop.syncStyle();
        }
    };
});
