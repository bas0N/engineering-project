import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { PrimaryButton, TextField, Text, Stack } from "@fluentui/react";
import { ChatInputWrapper, MessagesWrapper, MessageBubble } from "./ChatDetailPage.styled";
import axios from "axios";
import { ChatService } from "../../../service/ChatService.ts";
import { MessagesResponse, MessageResponse } from "../../../Chat.types.ts";

interface ChatDetailPageProps {
    currentUserId: string;
}

const ChatDetailPage: React.FC<ChatDetailPageProps> = ({ currentUserId }) => {
    const { receiverId } = useParams();
    const [messages, setMessages] = useState<MessageResponse[]>([]);
    const [newMessage, setNewMessage] = useState('');
    const [unreadCount, setUnreadCount] = useState<number>(0);

    const chatServiceRef = useRef<ChatService | null>(null);

    useEffect(() => {
        const token = localStorage.getItem('authToken');
        console.log('Token:', token);
        console.log('Username:', receiverId);
        if (!token || !receiverId) return;

        loadMessages(token, receiverId);

        chatServiceRef.current = new ChatService({
            token,
            onMessage: (msg: MessageResponse) => {
                if (msg.senderId === receiverId || msg.receiverId === receiverId) {
                    setMessages((prev) => [...prev, msg]);
                }
            },
        });
        chatServiceRef.current?.activate()

        // 3. Pobierz globalny unreadCount (opcjonalnie)
        fetchGlobalUnreadCount(token);

        return () => {
            chatServiceRef.current?.deactivate();
        };
    }, [receiverId]);

    const loadMessages = async (token: string, contactId: string) => {
        try {
            const resp = await axios.get<MessagesResponse>(
                `${import.meta.env.VITE_API_URL}/message/${contactId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setMessages(resp.data.messages);

            const unreadIds = resp.data.messages
                .filter(m => !m.isRead && m.receiverId === currentUserId)
                .map(m => m.uuid);

            if (unreadIds.length > 0) {
                await axios.post(
                    `${import.meta.env.VITE_API_URL}/message/markAsRead`,
                    unreadIds,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
            }
        } catch (err) {
            console.error('Error loading messages', err);
        }
    };

    const fetchGlobalUnreadCount = async (token: string) => {
        try {
            const resp = await axios.get<number>(
                `${import.meta.env.VITE_API_URL}/message/unreadCount`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setUnreadCount(resp.data);
        } catch (error) {
            console.error('Error fetching unread count', error);
        }
    };

    const handleSendMessage = () => {
        if (!newMessage.trim() || !chatServiceRef.current || !receiverId) {
            return;
        }
        console.log('Sending message:', newMessage);
        chatServiceRef.current?.sendMessage(newMessage.trim(), receiverId);
        setNewMessage('');
    };

    return (
        <Stack style={{ padding: 16 }}>
            <Text variant="large">Chat with {receiverId}</Text>
            <Text variant="small">Global unread count: {unreadCount}</Text>

            <MessagesWrapper>
                {messages.map((msg) => {
                    const isOwn = msg.senderId === currentUserId;
                    return (
                        <MessageBubble key={msg.uuid} isOwn={isOwn}>
                            <strong>{isOwn ? 'Ty' : msg.senderId}</strong>: {msg.content}
                            <div style={{ fontSize: '0.8em', marginTop: 4 }}>
                                {msg.dateAdded}
                                {msg.isRead ? '' : ' (unread)'}
                            </div>
                        </MessageBubble>
                    );
                })}
            </MessagesWrapper>

            <ChatInputWrapper>
                <TextField
                    value={newMessage}
                    onChange={(_, val) => setNewMessage(val || '')}
                    placeholder="Enter message..."
                    styles={{
                        fieldGroup: { backgroundColor: '#fff' },
                        root: { flex: 1, marginRight: 8 },
                    }}
                />
                <PrimaryButton text="Send" onClick={handleSendMessage} />
            </ChatInputWrapper>
        </Stack>
    );
};

export default ChatDetailPage;
