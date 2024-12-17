import {useEffect, useState} from 'react';
import axios from 'axios';
import {ItemResponse} from '../../Order.types.ts';
import {
    Spinner,
    Text,
    Table,
    TableCell,
    TableBody,
    TableHeader,
    TableRow,
} from "@fluentui/react-components";
import { useOrderHistoryStyles } from "./OrderHistory.styled.tsx";

interface Order {
    orderId: string;
    status: string;
    items: ItemResponse[];
    summaryPrice: number;
}

export default function OrderHistory() {
    const [orders, setOrders] = useState<Order[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                const token = localStorage.getItem('authToken');
                const response = await axios.get<Order[]>(`${import.meta.env.VITE_API_URL}order`, {
                    headers: {'Authorization': `Bearer ${token}`}
                });
                console.log(response.data);
                setOrders(response.data);
                setLoading(false);
            } catch {
                setError('Failed to fetch orders');
                setLoading(false);
            }
        };
        fetchOrders();
    }, []);
    const styles = useOrderHistoryStyles();

    if (loading) {
        return <Spinner label="Loading orders..."/>;
    }

    if (error) {
        return <Text style={{color: "red"}}>{error}</Text>;
    }

    return (
        <div className={styles.container}>
            <Text className={styles.heading}>Order History</Text>
            {orders.length === 0 ? (
                <Text>No orders found.</Text>
            ) : (
                orders.map((order) => (
                    <div className={styles.card} key={order.orderId}>
                        <div className={styles.cardHeader}>
                            <Text weight="semibold">Order ID: {order.orderId}</Text>
                        </div>
                        <div className={styles.cardContent}>
                            <Text>
                                <strong>Status:</strong> {order.status}
                            </Text>
                            <Table aria-label="Order Items" className={styles.table}>
                                <TableHeader>
                                    <TableRow>
                                        <TableCell>Name</TableCell>
                                        <TableCell>Image</TableCell>
                                        <TableCell>Quantity</TableCell>
                                        <TableCell>Price Unit</TableCell>
                                        <TableCell>Price Summary</TableCell>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {order.items.map((item, index) => (
                                        <TableRow
                                            key={item.uuid}
                                            className={index % 2 === 0 ? styles.tableRowEven : styles.tableRowOdd}
                                        >
                                            <TableCell>{item.name}</TableCell>
                                            <TableCell>
                                                <img
                                                    src={item.imageUrl}
                                                    alt={item.name}
                                                    className={styles.image}
                                                />
                                            </TableCell>
                                            <TableCell>{item.quantity}</TableCell>
                                            <TableCell>{item.priceUnit} PLN</TableCell>
                                            <TableCell>{item.priceSummary} PLN</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </div>
                        <div className={styles.cardFooter}>
                            <Text>Total Price: {order.summaryPrice} PLN</Text>
                        </div>
                    </div>
                ))
            )}
        </div>
    );
}
