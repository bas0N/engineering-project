import { makeStyles, shorthands } from "@fluentui/react-components";

export const useOrderHistoryStyles = makeStyles({
    container: {
        maxWidth: "800px",
        margin: "40px auto", // Increased top and bottom margin
        backgroundColor: "#1e1e1e",
        color: "#f1f1f1",
        padding: "20px",
        ...shorthands.borderRadius("10px"),
        boxShadow: "0 4px 10px rgba(0, 0, 0, 0.2)",
    },
    heading: {
        marginBottom: "30px", // Increased spacing between the heading and the cards
        fontSize: "24px", // Larger font for the heading
        fontWeight: 600,
        color: "#ffffff",
    },
    card: {
        marginBottom: "25px", // Added more space between cards
        backgroundColor: "#2a2a2a",
        color: "#f1f1f1",
        ...shorthands.borderRadius("10px"),
        boxShadow: "0 2px 5px rgba(0, 0, 0, 0.1)",
    },
    cardHeader: {
        padding: "15px 20px",
        borderBottom: "1px solid #444",
        fontSize: "16px",
        fontWeight: 600,
        color: "#dcdcdc",
    },
    cardContent: {
        padding: "20px",
        display: "flex",
        flexDirection: "column",
        gap: "15px", // Added space between the status, date, and table
    },
    cardFooter: {
        padding: "15px 20px",
        borderTop: "1px solid #444",
        textAlign: "right",
        fontWeight: 600,
        color: "#f1f1f1",
    },
    table: {
        width: "100%",
        borderCollapse: "collapse",
        margin: "10px 0", // Adjusted margin
    },
    tableCell: {
        border: "1px solid #444",
        padding: "10px",
        textAlign: "left",
        fontSize: "14px",
        color: "#dcdcdc",
    },
    tableHeader: {
        backgroundColor: "#333",
        fontWeight: "600",
        textTransform: "uppercase",
    },
    tableRowOdd: {
        backgroundColor: "#292929",
    },
    tableRowEven: {
        backgroundColor: "#1f1f1f",
    },
    tableRowHover: {
        ":hover": {
            backgroundColor: "#383838",
        },
    },
    image: {
        maxWidth: "80px",
        height: "auto",
        borderRadius: "5px",
        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.5)",
    },
});
