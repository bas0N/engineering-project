import {Text} from "@fluentui/react-components";
interface OrderSummaryProps {
    totalPrice: number;
    summaryLabel: string;
    totalLabel: string;
}

export function OrderSummary({ totalPrice, summaryLabel, totalLabel }: OrderSummaryProps) {
    return (
        <div style={{marginTop:'20px'}}>
            <Text>{summaryLabel}</Text>
            <Text>{totalLabel}: {totalPrice} PLN</Text>
        </div>
    );
}