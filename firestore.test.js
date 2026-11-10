const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

const PROJECT_ID = "demo-no-project";
const DATABASE_ID = "ai-studio-android-mkshops-0d9bf092-e6f5-438b-a9fc-7498d21ca10b";

let testEnv;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      host: "127.0.0.1",
      port: 8085,
      databaseId: DATABASE_ID,
      rules: fs.readFileSync("firestore.rules", "utf8")
    }
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read products", async () => {
  const unauthedDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthedDb.collection("products").get());
});

test("Authenticated user can read and create valid products", async () => {
  const authedDb = testEnv.authenticatedContext("user123").firestore();
  await assertSucceeds(
    authedDb.collection("products").doc("prod1").set({
      name: "Smart Watch MK-X",
      category: "Gadgets",
      price: 2490,
      discountPrice: 1990,
      stock: 50,
      description: "Premium Amoled Display smartwatch",
      status: "in_stock"
    })
  );

  const snap = await assertSucceeds(authedDb.collection("products").doc("prod1").get());
  if (!snap.exists) throw new Error("Product doc should exist");
});

test("Owner can create and read their user profile, others cannot", async () => {
  const user1Db = testEnv.authenticatedContext("user_1").firestore();
  const user2Db = testEnv.authenticatedContext("user_2").firestore();

  await assertSucceeds(
    user1Db.collection("users").doc("user_1").set({
      userId: "user_1",
      email: "test@example.com",
      displayName: "Test User",
      role: "customer"
    })
  );

  const snap = await assertSucceeds(
    user1Db.collection("users").doc("user_1").get()
  );
  if (!snap.exists) throw new Error("User doc should exist");

  // user_2 reading user_1 profile fails
  await assertFails(
    user2Db.collection("users").doc("user_1").get()
  );
});

test("Cross-user isolation: user1 cannot write to user2's orders or profile", async () => {
  const user1Db = testEnv.authenticatedContext("user_1").firestore();
  const user2Db = testEnv.authenticatedContext("user_2").firestore();

  // user1 writing to user1 order: succeeds
  await assertSucceeds(
    user1Db.collection("users").doc("user_1").collection("orders").doc("ord_1").set({
      userId: "user_1",
      voucherNumber: "MK-ORD-1001",
      customerName: "Rahim Ahmed",
      customerPhone: "01765820192",
      shippingAddress: "Dhanmondi, Dhaka",
      totalAmount: 2060,
      orderStatus: "New"
    })
  );

  // user2 trying to read or write user1's orders: FAILS
  await assertFails(
    user2Db.collection("users").doc("user_1").collection("orders").doc("ord_1").get()
  );

  await assertFails(
    user2Db.collection("users").doc("user_1").collection("orders").doc("ord_fake").set({
      userId: "user_1",
      voucherNumber: "MK-ORD-HACK",
      customerName: "Hacker",
      customerPhone: "01700000000",
      shippingAddress: "Unknown",
      totalAmount: 100,
      orderStatus: "New"
    })
  );
});
