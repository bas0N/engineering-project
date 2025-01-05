import { useEffect, useRef, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axios from 'axios';
import {
    ChatDetailContainer,
    ChatHeader,
    MessagesContainer,
    MessageBubble,
    ChatInputContainer,
    ChatTextInput,
    SendButton,
} from './ChatDetailPage.styled';
import { Text } from '@fluentui/react-components';
import {MessagesResponse, MessageResponse } from '../../Chat.types';

export const ChatDetailPage = () => {
    const { receiverId } = useParams();
    const [messages, setMessages] = useState<MessageResponse[]>([]);
    const [newMessage, setNewMessage] = useState('');
    const [receiverName, setReceiverName] = useState('Loading...');
    const stompClientRef = useRef<Client | null>(null);
    const token = localStorage.getItem('authToken');

    useEffect(() => {
        if (!receiverId) return;

        const fetchMessages = async () => {
            try {
                const res = await axios.get<MessagesResponse>(
                    `${import.meta.env.VITE_API_URL}message/${receiverId}`,
                    {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                        },
                    }
                );

                setMessages(res.data.messages);

                const receiver = res.data.receiver;
                let name = receiver.username || receiver.email || 'Unknown User';
                setReceiverName(name);
            } catch (err) {
                console.error('Error fetching messages:', err);
            }
        };

        fetchMessages();
    }, [receiverId]);

    useEffect(() => {
        if (!receiverId) return;

        const client = new Client({
            webSocketFactory: () => new SockJS(import.meta.env.VITE_CHAT_URL),
            debug: (str) => console.log('[STOMP DEBUG]', str),
        });

        client.onConnect = () => {
            console.log('STOMP connected');
            client.subscribe('/user/queue/messages', (message) => {
                const body = JSON.parse(message.body) as MessageResponse;
                if (body.senderId === receiverId || body.receiverId === receiverId) {
                    setMessages((prev) => [...prev, body]);
                }
            });
        };

        client.onStompError = (frame) => {
            console.error('Broker reported error:', frame.headers['message']);
            console.error('Additional details:', frame.body);
        };

        client.activate();
        stompClientRef.current = client;

        return () => {
            if (stompClientRef.current) {
                stompClientRef.current?.deactivate();
            }
        };
    }, [receiverId]);

    const sendMessage = () => {
        if (!newMessage.trim() || !receiverId) return;
        if (!stompClientRef.current?.active) {
            console.error('STOMP client not connected');
            return;
        }

        const msgBody = {
            content: newMessage.trim(),
            receiverId,
        };

        stompClientRef.current?.publish({
            destination: '/app/sendMessage',
            body: JSON.stringify(msgBody),
            headers: {
                'Authorization': `Bearer ${token || ''}`,
            },
        });

        setNewMessage('');
    };

    return (
        <ChatDetailContainer>
            <ChatHeader>
                <Text size={500} weight="semibold">
                    Chat with: {receiverName}
                </Text>
            </ChatHeader>

            <MessagesContainer>
                {messages.length === 0 ? (
                    <Text size={300} block>
                        No messages yet. Start the conversation!
                    </Text>
                ) : (
                    messages.map((msg) => {
                        const isOwn = msg.senderId !== receiverId;

                        return (
                            <MessageBubble key={msg.uuid} isOwn={isOwn}>
                                <Text>{msg.content}</Text>
                                <Text size={200} block style={{ marginTop: 4 }}>
                                    {new Date(msg.dateAdded).toLocaleTimeString()}
                                </Text>
                            </MessageBubble>
                        );
                    })
                )}
            </MessagesContainer>

            <ChatInputContainer>
                <ChatTextInput
                    placeholder="Type a message..."
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.currentTarget.value)}
                />
                <SendButton appearance="primary" onClick={sendMessage}>
                    Send
                </SendButton>
            </ChatInputContainer>
        </ChatDetailContainer>
    );
};
