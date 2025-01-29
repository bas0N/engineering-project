import { ChangeEvent, useCallback, useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { Button, Text, Textarea, TextareaOnChangeData } from "@fluentui/react-components";
import { ChatInputWrapper, MessagesWrapper, MessageBubble, ChatContainer } from "./ChatDetailPage.styled";
import axios from "axios";
import { ChatService } from "../../../service/ChatService";
import { MessagesResponse, MessageResponse, UserDetailsResponse } from "../../../Chat.types";

const ChatDetailPage = () => {
    const { receiverId } = useParams();
    const token = localStorage.getItem('authToken');

    // Przechowujemy aktualnego usera w stanie
    const [currentUserId, setCurrentUserId] = useState<string>('');
    const [messages, setMessages] = useState<MessageResponse[]>([]);
    const [newMessage, setNewMessage] = useState('');
    const [unreadCount, setUnreadCount] = useState<number>(0);
    const [senderInfo, setSenderInfo] = useState<UserDetailsResponse | null>(null);
    const [receiverInfo, setReceiverInfo] = useState<UserDetailsResponse | null>(null);
    const messagesEndRef = useRef<HTMLDivElement | null>(null);
    const chatServiceRef = useRef<ChatService | null>(null);

    /**
     * 1. useEffect do pobrania currentUserId z backendu.
     *    Robimy to raz – po załadowaniu komponentu.
     */

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    useEffect(() => {
        const fetchUserId = async () => {
            if (!token) return;
            try {
                const resp = await axios.get<UserDetailsResponse>(
                    `${import.meta.env.VITE_API_URL}auth/user/details`,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                console.log('User details:', resp.data);
                setCurrentUserId(resp.data.id);
            } catch (err) {
                console.error('Error fetching user details', err);
            }
        };

        fetchUserId();
    }, [token]);


    const loadMessages = useCallback(async (token: string, contactId: string) => {
        try {
            const resp = await axios.get<MessagesResponse>(
                `${import.meta.env.VITE_API_URL}message/${contactId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setMessages(resp.data.messages);
            setSenderInfo(resp.data.sender);
            setReceiverInfo(resp.data.receiver);

            // Oznacz jako przeczytane (opcjonalnie)
            const unreadIds = resp.data.messages
                .filter(m => !m.isRead && m.receiverId === currentUserId)
                .map(m => m.uuid);

            if (unreadIds.length > 0) {
                await axios.post(
                    `${import.meta.env.VITE_API_URL}message/markAsRead`,
                    unreadIds,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
            }
        } catch (err) {
            console.error('Error loading messages', err);
        }
    }, [currentUserId]);

    /**
     * 2. useEffect do głównej logiki chatu (STOMP + loadMessages + unreadCount).
     *    Uruchamiamy dopiero, gdy mamy currentUserId oraz receiverId.
     */
    useEffect(() => {

        if (!token || !receiverId || !currentUserId) {
            return;
        }

        console.log('currentUserId:', currentUserId, 'receiverId:', receiverId);

        loadMessages(token, receiverId);

        console.log('Activating chat service...');
        chatServiceRef.current = new ChatService({
            token,
            onMessage: (msg: MessageResponse) => {
                console.log('Received message:', msg);
                if (
                    (msg.senderId === receiverId && msg.receiverId === currentUserId) ||
                    (msg.senderId === currentUserId && msg.receiverId === receiverId)
                ) {
                    setMessages(prev => [...prev, msg]);
                }
            },
        });

        chatServiceRef.current.activate();
        console.log('Chat service activated');

        fetchGlobalUnreadCount(token);

        return () => {
            console.log('Deactivating chat service...');
            chatServiceRef.current?.deactivate();
        };
    }, [receiverId, currentUserId, token, loadMessages]);


    /**
     * Pobiera globalną liczbę nieprzeczytanych wiadomości
     */
    const fetchGlobalUnreadCount = async (token: string) => {
        try {
            const resp = await axios.get<number>(
                `${import.meta.env.VITE_API_URL}message/unreadCount`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setUnreadCount(resp.data);
        } catch (error) {
            console.error('Error fetching unread count', error);
        }
    };

    /**
     * Wysyłanie wiadomości STOMP
     */
    const handleSendMessage = () => {
        if (!newMessage.trim() || !chatServiceRef.current || !receiverId) {
            return;
        }
        console.log('Sending message:', newMessage);

        if (chatServiceRef.current instanceof ChatService) {
            chatServiceRef.current.sendMessage(newMessage.trim(), receiverId);
        }

        loadMessages(token || '', receiverId);
    };

    /**
     * Funkcja pomocnicza do wyświetlania nazwy użytkownika
     */
    const getDisplayName = (userId: string) => {
        if (userId === currentUserId) {
            return "You";
        }

        console.log('senderInfo:', senderInfo, 'receiverInfo:', receiverInfo);

        // Sprawdzenie receiverInfo
        if (receiverInfo && userId === receiverInfo.userId) {
            const name = receiverInfo.username?.trim();
            return (name && name !== "null null") ? name : receiverInfo.email || "Unknown user";
        }

        // Sprawdzenie senderInfo
        if (senderInfo && userId === senderInfo.userId) {
            const name = senderInfo.username?.trim();
            return (name && name !== "null null") ? name : senderInfo.email || "Unknown user";
        }

        return "Unknown user";
    };


    return (
        <ChatContainer>
            <section style={{ display: 'flex', flexDirection: 'column', padding: 16 }}>
                <Text variant="large">
                    Chat with {getDisplayName(receiverId || '')}
                </Text>
                <Text variant="small">Global unread count: {unreadCount}</Text>

                <MessagesWrapper>
                    {messages.map((msg) => {
                        const isOwn = (msg.senderId === currentUserId);
                        const displayName = getDisplayName(msg.senderId);

                        return (
                            <MessageBubble key={msg.uuid} isOwn={isOwn}>
                                <strong>{displayName}</strong>: {msg.content}
                                <div style={{fontSize: '0.8em', marginTop: 4}}>
                                    {msg.dateAdded}
                                    {!msg.isRead && ' (unread)'}
                                </div>
                            </MessageBubble>
                        );
                    })}
                    <div ref={messagesEndRef}/>
                </MessagesWrapper>

                <ChatInputWrapper>
                    <Textarea
                        value={newMessage}
                        onChange={(_ev: ChangeEvent<HTMLTextAreaElement>, val: TextareaOnChangeData) => setNewMessage(val.value || '')}
                        placeholder="Enter message..."
                    />
                    <Button onClick={handleSendMessage}>
                        Send
                    </Button>
                </ChatInputWrapper>
            </section>
        </ChatContainer>
    );
};

export default ChatDetailPage;
