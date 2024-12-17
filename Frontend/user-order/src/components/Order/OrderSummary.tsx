import {Text} from "@fluentui/react-components";
import {useOrderSummaryStyles} from "./OrderSummary.styled";
interface OrderSummaryProps {
    totalPrice: number;
    summaryLabel: string;
    totalLabel: string;
}

export function OrderSummary({ totalPrice, summaryLabel, totalLabel }: OrderSummaryProps) {
    const styles = useOrderSummaryStyles();

    return (
        <div className={styles.container}>
            <Text className={styles.label}>{summaryLabel}</Text>
            <Text>{totalLabel}: {totalPrice} PLN</Text>
        </div>
    );
}