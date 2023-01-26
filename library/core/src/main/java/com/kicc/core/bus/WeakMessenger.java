package com.kicc.core.bus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.kicc.core.binding.command.BindingAction;
import com.kicc.core.binding.command.BindingConsumer;

/**
 *<p>
 * 全局组件通信
 * 与RxBus不同的是他支持任何类型的事件,比如字符串,整数来充当token来获取监听对象
 * 支持令牌,对象,目前使用场景,活动 碎片 视图模型 三层 工程库之间互相传递数据
 * https://github.com/Kelin-Hong/MVVMLight#messenger
 *</p>
 *
 * @Author: entfrm开发团队-王翔
 * @Date: 2022/6/23
 */
public class WeakMessenger {

    private static WeakMessenger defaultInstance;

    private HashMap<Type, List<WeakActionAndToken>> recipientsOfSubclassesAction;

    private HashMap<Type, List<WeakActionAndToken>> recipientsStrictAction;

    public static WeakMessenger getDefault() {
        if (defaultInstance == null) {
            defaultInstance = new WeakMessenger();
        }
        return defaultInstance;
    }


    public static void overrideDefault(WeakMessenger newWeakWeakMessenger) {
        defaultInstance = newWeakWeakMessenger;
    }

    public static void reset() {
        defaultInstance = null;
    }

    /**
     * @param recipient 接收方,如果在活动中注册,接收方总是设置 this
     *                  和 WeakMessenger.getDefault().unregister(this) 在onDestroy中,如果在ViewModel中
     *                  你也可以注册活动上下文,也可以在onDestroy取消注册
     * @param action    根据收到的信息做些什么
     */
    public void register(Object recipient, BindingAction action) {
        register(recipient, null, false, action);
    }

    /**
     * @param recipient                 接收方,如果在活动中注册,接收方总是设置 this
     *                                  和 WeakMessenger.getDefault().unregister(this) 在onDestroy中,如果在ViewModel中
     *                                  你也可以注册活动上下文,也可以在onDestroy取消注册
     * @param receiveDerivedMessagesToo 接收者的派生类是否可以接收消息
     * @param action                    根据收到的信息做些什么
     */
    public void register(Object recipient, boolean receiveDerivedMessagesToo, BindingAction action) {
        register(recipient, null, receiveDerivedMessagesToo, action);
    }

    /**
     * @param recipient 接收方,如果在活动中注册,接收方总是设置 this
     *                  和 WeakMessenger.getDefault().unregister(this) 在onDestroy中,如果在ViewModel中
     *                  你也可以注册活动上下文,也可以在onDestroy取消注册
     * @param token     注册一个唯一的令牌,当一个信使发送一个相同的令牌,它将会收到这个消息
     * @param action    根据收到的信息做些什么
     */
    public void register(Object recipient, Object token, BindingAction action) {
        register(recipient, token, false, action);
    }

    /**
     * @param recipient 接收方,如果在活动中注册,接收方总是设置 this
     *                  和 WeakMessenger.getDefault().unregister(this) 在onDestroy中,如果在ViewModel中
     *                  你也可以注册活动上下文,也可以在onDestroy取消注册
     * @param token     注册一个唯一的令牌,当一个信使发送一个相同的令牌,它将会收到这个消息
     * @param receiveDerivedMessagesToo 接收者的派生类是否可以接收消息
     * @param action                    根据收到的信息做些什么
     */
    public void register(Object recipient, Object token, boolean receiveDerivedMessagesToo, BindingAction action) {

        Type messageType = NotMsgType.class;

        HashMap<Type, List<WeakActionAndToken>> recipients;

        if (receiveDerivedMessagesToo) {
            if (recipientsOfSubclassesAction == null) {
                recipientsOfSubclassesAction = new HashMap();
            }

            recipients = recipientsOfSubclassesAction;
        } else {
            if (recipientsStrictAction == null) {
                recipientsStrictAction = new HashMap();
            }

            recipients = recipientsStrictAction;
        }

        List<WeakActionAndToken> list;

        if (!recipients.containsKey(messageType)) {
            list = new ArrayList();
            recipients.put(messageType, list);
        } else {
            list = recipients.get(messageType);
        }

        WeakAction weakAction = new WeakAction(recipient, action);

        WeakActionAndToken item = new WeakActionAndToken(weakAction, token);
        list.add(item);
        cleanup();
    }

    /**
     * @param recipient {}
     * @param tClass    类(T)
     * @param action    这个操作有一个tClass类型的参数
     * @param <T>       消息数据类型
     */
    public <T> void register(Object recipient, Class<T> tClass, BindingConsumer<T> action) {
        register(recipient, null, false, action, tClass);
    }

    /**
     * @param recipient                 接收的消息
     * @param receiveDerivedMessagesToo 接收者的派生类是否可以接收消息
     * @param tClass                    类(T)
     * @param action                    这个操作有一个tClass类型的参数
     * @param <T>                       消息数据类型
     */
    public <T> void register(Object recipient, boolean receiveDerivedMessagesToo, Class<T> tClass, BindingConsumer<T> action) {
        register(recipient, null, receiveDerivedMessagesToo, action, tClass);
    }

    /**
     * @param recipient 接收的消息
     * @param token     注册一个唯一的令牌,当一个信使发送一个相同的令牌,它将会收到这个消息
     * @param tClass    BindingConsumer的T类
     * @param action    这个操作有一个tClass类型的参数
     * @param <T>       消息数据类型
     */
    public <T> void register(Object recipient, Object token, Class<T> tClass, BindingConsumer<T> action) {
        register(recipient, token, false, action, tClass);
    }

    /**
     * @param recipient                 接收的消息
     * @param token                     注册一个唯一的令牌,当一个信使发送一个相同的令牌,它将会收到这个消息
     * @param receiveDerivedMessagesToo 接收者的派生类是否可以接收消息
     * @param action                    这个操作有一个tClass类型的参数
     * @param tClass                    BindingConsumer的T类
     * @param <T>                       消息数据类型
     */
    public <T> void register(Object recipient, Object token, boolean receiveDerivedMessagesToo, BindingConsumer<T> action, Class<T> tClass) {

        Type messageType = tClass;

        HashMap<Type, List<WeakActionAndToken>> recipients;

        if (receiveDerivedMessagesToo) {
            if (recipientsOfSubclassesAction == null) {
                recipientsOfSubclassesAction = new HashMap();
            }

            recipients = recipientsOfSubclassesAction;
        } else {
            if (recipientsStrictAction == null) {
                recipientsStrictAction = new HashMap();
            }

            recipients = recipientsStrictAction;
        }

        List<WeakActionAndToken> list;

        if (!recipients.containsKey(messageType)) {
            list = new ArrayList();
            recipients.put(messageType, list);
        } else {
            list = recipients.get(messageType);
        }

        WeakAction weakAction = new WeakAction(recipient, action);

        WeakActionAndToken item = new WeakActionAndToken(weakAction, token);
        list.add(item);
        cleanup();
    }


    private void cleanup() {
        cleanupList(recipientsOfSubclassesAction);
        cleanupList(recipientsStrictAction);
    }

    /**
     * @param token 发送一个唯一的令牌,当接收方注册了相同的令牌,它将收到这个MSG
     */
    public void sendNoMsg(Object token) {
        sendToTargetOrType(null, token);
    }

    /**
     * 直接发送给没有任何消息的收件人
     *
     * @param target WeakMessenger.getDefault(), 在一个活动中注册(this， ..) 如果target设置了这个活动,它将接收消息
     */
    public void sendNoMsgToTarget(Object target) {
        sendToTargetOrType(target.getClass(), null);
    }

    /**
     * 发送消息到目标标记,当接收方注册了相同的标记,它将收到这个消息
     *
     * @param token  发送一个唯一的令牌,当接收方注册了相同的令牌,它将收到这个MSG
     * @param target WeakMessenger.getDefault(), 在一个活动中注册(this， ..) 如果target设置了这个活动,它将接收消息
     */
    public void sendNoMsgToTargetWithToken(Object token, Object target) {
        sendToTargetOrType(target.getClass(), token);
    }

    /**
     * 发送消息类型为T,所有接收方都可以收到该消息
     *
     * @param message 任何对象都可以是消息
     * @param <T>     消息数据类型
     */
    public <T> void send(T message) {
        sendToTargetOrType(message, null, null);
    }

    /**
     * 发送消息类型为T,所有接收方都可以收到该消息
     *
     * @param message 任何对象都可以是消息
     * @param token   发送一个唯一的令牌,当接收方注册了相同的令牌,它将收到这条消息
     * @param <T>     消息数据类型
     */
    public <T> void send(T message, Object token) {
        sendToTargetOrType(message, null, token);
    }

    /**
     * 直接发送邮件给收件人
     *
     * @param message 任何对象都可以是消息
     * @param target  WeakMessenger.getDefault(), 在一个活动中注册(this， ..) 如果target设置了这个活动,它将接收消息
     * @param <T>     消息数据类型
     * @param <R>     目标
     */
    public <T, R> void sendToTarget(T message, R target) {
        sendToTargetOrType(message, target.getClass(), null);
    }

    /**
     * 取消注册接收器,如:WeakMessenger.getDefault().unregister(this) 在onDestroy在活动中是必需的,以避免内存泄漏
     *
     * @param recipient 接收的消息
     */
    public void unregister(Object recipient) {
        unregisterFromLists(recipient, recipientsOfSubclassesAction);
        unregisterFromLists(recipient, recipientsStrictAction);
        cleanup();
    }


    public <T> void unregister(Object recipient, Object token) {
        unregisterFromLists(recipient, token, null, recipientsStrictAction);
        unregisterFromLists(recipient, token, null, recipientsOfSubclassesAction);
        cleanup();
    }


    private static <T> void sendToList(
            T message,
            Collection<WeakActionAndToken> list,
            Type messageTargetType,
            Object token) {
        if (list != null) {
            // 克隆保护人们注册在一个接收消息的方法
            // Bug修正消息BL0004.007
            ArrayList<WeakActionAndToken> listClone = new ArrayList<>();
            listClone.addAll(list);

            for (WeakActionAndToken item : listClone) {
                WeakAction executeAction = item.getAction();
                if (executeAction != null
                        && item.getAction().isLive()
                        && item.getAction().getTarget() != null
                        && (messageTargetType == null
                        || item.getAction().getTarget().getClass() == messageTargetType
                        || classImplements(item.getAction().getTarget().getClass(), messageTargetType))
                        && ((item.getToken() == null && token == null)
                        || item.getToken() != null && item.getToken().equals(token))) {
                    executeAction.execute(message);
                }
            }
        }
    }

    private static void unregisterFromLists(Object recipient, HashMap<Type, List<WeakActionAndToken>> lists) {
        if (recipient == null
                || lists == null
                || lists.size() == 0) {
            return;
        }
        synchronized (lists) {
            for (Type messageType : lists.keySet()) {
                for (WeakActionAndToken item : lists.get(messageType)) {
                    WeakAction weakAction = item.getAction();

                    if (weakAction != null
                            && recipient == weakAction.getTarget()) {
                        weakAction.markForDeletion();
                    }
                }
            }
        }
        cleanupList(lists);
    }

    private static <T> void unregisterFromLists(
            Object recipient,
            BindingConsumer<T> action,
            HashMap<Type, List<WeakActionAndToken>> lists,
            Class<T> tClass) {
        Type messageType = tClass;

        if (recipient == null
                || lists == null
                || lists.size() == 0
                || !lists.containsKey(messageType)) {
            return;
        }

        synchronized (lists) {
            for (WeakActionAndToken item : lists.get(messageType)) {
                WeakAction<T> weakActionCasted = (WeakAction<T>) item.getAction();

                if (weakActionCasted != null
                        && recipient == weakActionCasted.getTarget()
                        && (action == null
                        || action == weakActionCasted.getBindingConsumer())) {
                    item.getAction().markForDeletion();
                }
            }
        }
    }

    private static void unregisterFromLists(
            Object recipient,
            BindingAction action,
            HashMap<Type, List<WeakActionAndToken>> lists
    ) {
        Type messageType = NotMsgType.class;

        if (recipient == null
                || lists == null
                || lists.size() == 0
                || !lists.containsKey(messageType)) {
            return;
        }

        synchronized (lists) {
            for (WeakActionAndToken item : lists.get(messageType)) {
                WeakAction weakActionCasted = (WeakAction) item.getAction();

                if (weakActionCasted != null
                        && recipient == weakActionCasted.getTarget()
                        && (action == null
                        || action == weakActionCasted.getBindingAction())) {
                    item.getAction().markForDeletion();
                }
            }
        }
    }


    private static <T> void unregisterFromLists(
            Object recipient,
            Object token,
            BindingConsumer<T> action,
            HashMap<Type, List<WeakActionAndToken>> lists, Class<T> tClass) {
        Type messageType = tClass;

        if (recipient == null
                || lists == null
                || lists.size() == 0
                || !lists.containsKey(messageType)) {
            return;
        }

        synchronized (lists) {
            for (WeakActionAndToken item : lists.get(messageType)) {
                WeakAction<T> weakActionCasted = (WeakAction<T>) item.getAction();

                if (weakActionCasted != null
                        && recipient == weakActionCasted.getTarget()
                        && (action == null
                        || action == weakActionCasted.getBindingConsumer())
                        && (token == null
                        || token.equals(item.getToken()))) {
                    item.getAction().markForDeletion();
                }
            }
        }
    }

    private static void unregisterFromLists(
            Object recipient,
            Object token,
            BindingAction action,
            HashMap<Type, List<WeakActionAndToken>> lists) {
        Type messageType = NotMsgType.class;

        if (recipient == null
                || lists == null
                || lists.size() == 0
                || !lists.containsKey(messageType)) {
            return;
        }

        synchronized (lists) {
            for (WeakActionAndToken item : lists.get(messageType)) {
                WeakAction weakActionCasted = (WeakAction) item.getAction();

                if (weakActionCasted != null
                        && recipient == weakActionCasted.getTarget()
                        && (action == null
                        || action == weakActionCasted.getBindingAction())
                        && (token == null
                        || token.equals(item.getToken()))) {
                    item.getAction().markForDeletion();
                }
            }
        }
    }

    private static boolean classImplements(Type instanceType, Type interfaceType) {
        if (interfaceType == null
                || instanceType == null) {
            return false;
        }
        Class[] interfaces = ((Class) instanceType).getInterfaces();
        for (Class currentInterface : interfaces) {
            if (currentInterface == interfaceType) {
                return true;
            }
        }

        return false;
    }

    private static void cleanupList(HashMap<Type, List<WeakActionAndToken>> lists) {
        if (lists == null) {
            return;
        }
        for (Iterator it = lists.entrySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            List<WeakActionAndToken> itemList = lists.get(key);
            if (itemList != null) {
                for (WeakActionAndToken item : itemList) {
                    if (item.getAction() == null
                            || !item.getAction().isLive()) {
                        itemList.remove(item);
                    }
                }
                if (itemList.size() == 0) {
                    lists.remove(key);
                }
            }
        }
    }

    private void sendToTargetOrType(Type messageTargetType, Object token) {
        Class messageType = NotMsgType.class;
        if (recipientsOfSubclassesAction != null) {
            // 克隆保护人们注册在一个接收消息的方法
            // 错误修正消息BL0008.002
            // var listClone = recipientsOfSubclassesAction.Keys.Take(_recipientsOfSubclassesAction.Count()).ToList();
            List<Type> listClone = new ArrayList<>();
            listClone.addAll(recipientsOfSubclassesAction.keySet());
            for (Type type : listClone) {
                List<WeakActionAndToken> list = null;

                if (messageType == type
                        || ((Class) type).isAssignableFrom(messageType)
                        || classImplements(messageType, type)) {
                    list = recipientsOfSubclassesAction.get(type);
                }

                sendToList(list, messageTargetType, token);
            }
        }

        if (recipientsStrictAction != null) {
            if (recipientsStrictAction.containsKey(messageType)) {
                List<WeakActionAndToken> list = recipientsStrictAction.get(messageType);
                sendToList(list, messageTargetType, token);
            }
        }

        cleanup();
    }

    private static void sendToList(
            Collection<WeakActionAndToken> list,
            Type messageTargetType,
            Object token) {
        if (list != null) {
            // 克隆保护人们注册在一个接收消息的方法
            // Bug修正消息BL0004.007
            ArrayList<WeakActionAndToken> listClone = new ArrayList<>();
            listClone.addAll(list);

            for (WeakActionAndToken item : listClone) {
                WeakAction executeAction = item.getAction();
                if (executeAction != null
                        && item.getAction().isLive()
                        && item.getAction().getTarget() != null
                        && (messageTargetType == null
                        || item.getAction().getTarget().getClass() == messageTargetType
                        || classImplements(item.getAction().getTarget().getClass(), messageTargetType))
                        && ((item.getToken() == null && token == null)
                        || item.getToken() != null && item.getToken().equals(token))) {
                    executeAction.execute();
                }
            }
        }
    }

    private <T> void sendToTargetOrType(T message, Type messageTargetType, Object token) {
        Class messageType = message.getClass();


        if (recipientsOfSubclassesAction != null) {
            // 克隆保护人们注册在一个接收消息的方法
            // 错误修正消息BL0008.002
            // var listClone = recipientsOfSubclassesAction.Keys.Take(_recipientsOfSubclassesAction.Count()).ToList();
            List<Type> listClone = new ArrayList<>();
            listClone.addAll(recipientsOfSubclassesAction.keySet());
            for (Type type : listClone) {
                List<WeakActionAndToken> list = null;

                if (messageType == type
                        || ((Class) type).isAssignableFrom(messageType)
                        || classImplements(messageType, type)) {
                    list = recipientsOfSubclassesAction.get(type);
                }

                sendToList(message, list, messageTargetType, token);
            }
        }

        if (recipientsStrictAction != null) {
            if (recipientsStrictAction.containsKey(messageType)) {
                List<WeakActionAndToken> list = recipientsStrictAction.get(messageType);
                sendToList(message, list, messageTargetType, token);
            }
        }

        cleanup();
    }

    private class WeakActionAndToken {
        private WeakAction action;
        private Object token;

        public WeakActionAndToken(WeakAction action, Object token) {
            this.action = action;
            this.token = token;
        }

        public WeakAction getAction() {
            return action;
        }

        public void setAction(WeakAction action) {
            this.action = action;
        }

        public Object getToken() {
            return token;
        }

        public void setToken(Object token) {
            this.token = token;
        }
    }

    public static class NotMsgType { }
}
