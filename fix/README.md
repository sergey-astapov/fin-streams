# FIX 4.2 Examples

## Trade Capture Report (MsgType = AE) - Netted Trade

This message is used to report netted trades between counterparties.

### Example: Trade Capture Report for Netted Trade

```
8=FIX.4.2|35=AE|49=TRADER|56=BROKER|34=9|52=20230915-14:10:10.123|17=2001|150=0|55=AAPL|54=1|32=300|31=150.50|6=150.00|15=USD|60=20230915-14:10:10.000|10=104|
```

### Explanation:
- `35=AE`: MsgType for Trade Capture Report.
- `49=TRADER`: SenderCompID (Trader).
- `56=BROKER`: TargetCompID (Broker).
- `34=9`: MsgSeqNum (sequence number).
- `52=20230915-14:10:10.123`: SendingTime (timestamp).
- `17=2001`: ExecID (Unique execution ID).
- `150=0`: ExecType (0 = New).
- `55=AAPL`: Symbol (Apple).
- `54=1`: Side (Buy).
- `32=300`: LastShares (Netted quantity).
- `31=150.50`: LastPx (Price for the netted trade).
- `6=150.00`: AvgPx (Average price).
- `15=USD`: Currency.
- `60=20230915-14:10:10.000`: TransactTime.
- `10=104`: Checksum.

In this case, the Trade Capture Report reflects that 300 shares of `AAPL` were netted, with a net price of `150.50`. The field `6` (AvgPx) represents the average price of all trades before netting. This structure allows counterparties to settle the netted amount without needing to handle each individual trade.

Additional Tags to Consider:
- `37`: OrderID (the broker-assigned ID for the original order).
- `73`: NoOrders (number of orders associated with the trades being netted, if relevant).
- `64`: SettlDate (the settlement date of the trade).
- `75`: TradeDate (the trade date of the original trades).

Key Considerations:

- `Netted Quantity`: The `32` (LastShares) field represents the total quantity of shares netted.
- `Netted Price`: The `31` (LastPx) field represents the final price for the netted trade.
- `Average Price`: The `6` (AvgPx) field can be used to reflect the average price of the individual trades before they were netted.

*Scenario for True Netting:*

If there are multiple trades between two counterparties for the same symbol (e.g., `AAPL`), instead of settling each trade individually, the following occurs:

A buy of 200 shares at $150 and a buy of 100 shares at $151 are combined into a net buy of 300 shares at an average price of $150.50. This netted trade is reported using the Trade Capture Report.
This type of message flow would typically be managed by back-office systems responsible for post-trade processing and settlement. The FIX protocol facilitates the transmission of this information but does not enforce the netting logic itself.

---

To retrieve the details of an order using the FIX 4.2 protocol, you would use an Order Status Request message (MsgType = H). This message allows you to request the status of an existing order. The server responds with an Execution Report (MsgType = 8) containing the order details.

## Order Status Request (MsgType = H)

This message is used to request the status of a specific order, either by client order ID (`ClOrdID`) or the broker-assigned order ID (`OrderID`).

### Example: Order Status Request

```
8=FIX.4.2|35=H|49=TRADER|56=BROKER|34=7|52=20230915-13:10:05.123|11=12345|55=MSFT|54=1|10=150|
```

### Explanation:
- `35=H`: MsgType for Order Status Request.
- `49=TRADER`: SenderCompID (Trader making the request).
- `56=BROKER`: TargetCompID (Broker receiving the request).
- `34=7`: MsgSeqNum (sequence number).
- `52=20230915-13:10:05.123`: SendingTime (timestamp).
- `11=12345`: ClOrdID (Client's order ID).
- `55=MSFT`: Symbol (Microsoft).
- `54=1`: Side (Buy).
- `10=150`: Checksum (message validation).

The server would respond with an Execution Report (MsgType = 8), which contains the current status of the order (e.g., whether it’s filled, partially filled, or still open).

## Execution Report (MsgType = 8)

The server's response to the order status request is an Execution Report message, which gives the details of the order’s status.

```
8=FIX.4.2|35=8|49=BROKER|56=TRADER|34=8|52=20230915-13:10:20.234|37=54321|11=12345|17=1003|150=2|39=2|55=MSFT|54=1|38=100|44=250.00|32=100|31=250.00|151=0|14=100|10=221|
```

### Explanation:
- `35=8`: Execution Report message.
- `37=54321`: Order ID assigned by the broker or exchange.
- `11=12345`: Client Order ID (as sent in the original request).
- `17=1003`: Execution ID (unique identifier for this execution event).
- `150=2`: Execution type (2 = Filled).
- `39=2`: Order status (2 = Filled).
- `55=MSFT`: Symbol (Microsoft).
- `54=1`: Side (1 = Buy).
- `38=100`: Original order quantity.
- `44=250.00`: Price of the order.
- `32=100`: Quantity filled in this execution.
- `31=250.00`: Price of the fill.
- `151=0`: Remaining quantity (0 means fully filled).
- `14=100`: Cumulative quantity filled.
- `10=221`: Checksum.

This execution report provides detailed information about the status of the order, including the filled quantity, price, and remaining open quantity if applicable.

### Key Tags for Order Status:
- `11`: ClOrdID (Client Order ID)
- `37`: OrderID (assigned by the broker)
- `39`: OrdStatus (status of the order: New, Partially Filled, Filled, Canceled, etc.)
- `32`: LastShares (quantity filled in the last execution)
- `31`: LastPx (price of the last execution)
- `151`: LeavesQty (remaining quantity)
- `14`: CumQty (cumulative filled quantity)

---

## Execution Report (MsgType = 8) - Order Matched

An Execution Report is sent when an order is matched, detailing the execution.

In FIX 4.2, when an order is matched (i.e., executed, either fully or partially), the counterparty typically sends an Execution Report message (MsgType = 8). This message indicates that an order has been matched or filled. The report will specify details like the quantity, price, and status of the order.

Here’s how a matched order is communicated using the FIX 4.2 protocol.

*Execution Report (MsgType = 8)*

An Execution Report is sent when an order is matched, providing details such as the order status (`OrdStatus`), the executed quantity (`LastShares`), and the price at which the order was matched (`LastPx`).

### Example: Execution Report for a Matched Order

```
8=FIX.4.2|35=8|49=BROKER|56=TRADER|34=10|52=20230915-14:15:25.123|37=54321|11=12345|150=F|39=2|55=MSFT|54=1|38=100|44=250.00|32=100|31=250.00|151=0|14=100|10=099|
```

### Explanation:
- `35=8`: MsgType for Execution Report.
- `49=BROKER`: SenderCompID (Broker sending the report).
- `56=TRADER`: TargetCompID (Trader receiving the report).
- `34=10`: MsgSeqNum (message sequence number).
- `52=20230915-14:15:25.123`: SendingTime (timestamp).
- `37=54321`: OrderID (Broker's unique order ID).
- `11=12345`: ClOrdID (Client's order ID).
- `150=F`: ExecType (F = Trade, meaning matched).
- `39=2`: OrdStatus (2 = Filled).
- `55=MSFT`: Symbol (Microsoft).
- `54=1`: Side (Buy).
- `38=100`: OrderQty (Original quantity).
- `44=250.00`: Price.
- `32=100`: LastShares (Quantity matched).
- `31=250.00`: LastPx (Price matched).
- `151=0`: LeavesQty (Remaining to be filled).
- `14=100`: CumQty (Total matched).
- `10=099`: Checksum.

### Key Fields in Execution Report for Order Matched:
- `150`: ExecType
- `F = Trade`: This indicates the order has been executed (matched).
- `39`: OrdStatus
- `2 = Filled`: Order has been fully filled/matched.
- `1 = Partially Filled`: If only part of the order is matched.
- `32`: LastShares (Quantity of shares matched in this execution).
- `31`: LastPx (Price at which the order was matched).
- `151`: LeavesQty (Remaining quantity to be matched, 0 for fully matched orders).
- `14`: CumQty (Cumulative quantity that has been matched for the order).

---

## Execution Report (MsgType = 8) - Partially Matched

This message indicates a partial execution of the order.

If an order is only partially matched, the `LeavesQty` will be greater than 0, and `OrdStatus` will be set to `1 = Partially Filled`.

### Example: Partially Matched Execution Report

```
8=FIX.4.2|35=8|49=BROKER|56=TRADER|34=12|52=20230915-15:10:12.234|37=54323|11=12346|150=F|39=1|55=AAPL|54=1|38=1000|44=150.00|32=500|31=150.00|151=500|14=500|10=200|
```

### Explanation:
- `35=8`: MsgType for Execution Report.
- `49=BROKER`: SenderCompID (Broker).
- `56=TRADER`: TargetCompID (Trader).
- `34=12`: MsgSeqNum (sequence number).
- `52=20230915-15:10:12.234`: SendingTime (timestamp).
- `37=54323`: OrderID (Broker's order ID).
- `11=12346`: ClOrdID (Client's order ID).
- `150=F`: ExecType (F = Trade).
- `39=1`: OrdStatus (1 = Partially Filled).
- `55=AAPL`: Symbol (Apple).
- `54=1`: Side (Buy).
- `38=1000`: OrderQty (Original order size).
- `44=150.00`: Price.
- `32=500`: LastShares (Quantity matched).
- `31=150.00`: LastPx (Price matched).
- `151=500`: LeavesQty (Remaining shares).
- `14=500`: CumQty (Total shares matched so far).
- `10=200`: Checksum.

### Key Considerations:
When an order is fully matched (OrdStatus = `2`), `LeavesQty` is `0`, and `CumQty` equals the original order quantity.
For partial matches (OrdStatus = `1`), `LeavesQty` is greater than `0`, and `CumQty` reflects the portion of the order that has been filled so far.

### Fields Summary:
- `OrdStatus (Tag 39)`: Indicates the current state of the order (e.g., `1 = Partially Filled`, `2 = Filled`).
- `ExecType (Tag 150)`: Indicates the type of execution event (e.g., `F = Trade`).
- `LastShares (Tag 32)`: Number of shares matched in this specific execution.
- `LastPx (Tag 31)`: Price at which the shares were matched.
- `LeavesQty (Tag 151)`: Quantity still open for matching.
- `CumQty (Tag 14)`: Cumulative quantity matched so far.

---

## Execution Report (MsgType = 8) - Partially Matched with Remaining Canceled

This message shows that after part of an order is matched, the rest is canceled.

In FIX 4.2, a partially mismatched order, where part of the order is executed but the remaining part is either canceled or not filled, can be communicated using the Execution Report (MsgType = 8). The report would show that a portion of the order was executed and the remaining part was either canceled or left open.

A partially matched order (partial fill) typically includes:
- The executed portion.
- The remaining open quantity (if applicable).
- The status reflecting a partial fill.
- Here’s how to construct an Execution Report for a partially mismatched order.

Here’s how to construct an Execution Report for a partially mismatched order.

Execution Report for a Partially Matched Order (MsgType = 8)
When part of an order is matched, the Execution Report indicates the filled quantity, the price, and the remaining quantity.

### Example: Partial Match Followed by Cancel

```
8=FIX.4.2|35=8|49=BROKER|56=TRADER|34=13|52=20230915-15:12:15.567|37=54323|11=12346|150=4|39=4|55=AAPL|54=1|38=1000|32=500|31=150.00|151=0|14=500|41=12346|10=231|
```

### Explanation:
- `150=4`: ExecType (4 = Canceled).
- `39=4`: OrdStatus (4 = Canceled).
- `151=0`: LeavesQty (Remaining quantity is 0).
- `14=500`: CumQty (500 shares filled before cancel).
- `41=12346`: OrigClOrdID (Original Client Order ID being canceled).
- `10=231`: Checksum.

---
