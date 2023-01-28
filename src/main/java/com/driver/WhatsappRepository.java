package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;

    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception{
        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }else{
            User user = new User(name, mobile);
            userMobile.add(mobile);
            return "SUCCESS";
        }
    }

    public Group createGroup(List<User> users) {
        String groupName = "";
        if(users.size()>2){
            customGroupCount++;
            groupName = "Group "+customGroupCount;
        }else {
            groupName = users.get(1).getName();
        }
        Group group = new Group(groupName, users.size());
        groupUserMap.put(group, users);
        adminMap.put(group, users.get(0));
        return group;
    }

    public int createMessage(String content) {
        messageId++;
        Date date = new Date();
        Message message = new Message(messageId, content,date);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        List<User> userList = groupUserMap.get(group);
        if(!userList.contains(sender)){
            throw new Exception("You are not allowed to send message");
        }
        List<Message> messageList = groupMessageMap.get(group);
        messageList.add(message);
        groupMessageMap.put(group, messageList);
        senderMap.put(message,sender);
        return messageList.size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!Objects.equals(adminMap.get(group), approver)){
            throw new Exception("Approver does not have rights");
        }
        List<User> userList = groupUserMap.get(group);
        if(!userList.contains(user)){
            throw new Exception("User is not a participant");
        }
        User admin = adminMap.get(group);
        adminMap.remove(group);
        adminMap.put(group, user);
        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception{
        int flag = 0;
        Group userGroup = null;
        for(Group group: groupUserMap.keySet()){
            if(groupUserMap.get(group).contains(user)){
                userGroup = group;
                if(Objects.equals(adminMap.get(userGroup), user)){
                    throw new Exception("Cannot remove admin");
                }
                flag = 1;
            }
        }
        if (flag==0){
            throw new Exception("User not found");
        }
        return groupUserMap.get(userGroup).size()+groupUserMap.get(userGroup).size()+senderMap.size();
    }

    public String findMessage(Date start, Date end, int k) {
        return "SORRY";
    }
}
