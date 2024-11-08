import streamlit as st
import requests

# Define the URL and headers based on the Postman JSON
url = "http://localhost:8080/assistant/chat"
headers = {
    "Content-Type": "text/event-stream"
}

# Streamlit UI
st.title("Ollama Assistant Chat")
st.write("Enter your query below and press Send to receive a response.")

# User input
query = st.text_area("Query", placeholder="Enter your question here...", height=100)

# Button to trigger the POST request
if st.button("Send"):
    # Check if there is a query
    if query.strip():
        # Send POST request
        try:
            response = requests.post(url, headers=headers, data=query)

            # Display response
            if response.ok:
                st.write("Response:")
                st.text(response.text)
            else:
                st.error(f"Error: {response.status_code}")
                st.text(response.text)
        except Exception as e:
            st.error("Failed to connect to the server.")
            st.exception(e)
    else:
        st.warning("Please enter a query before sending.")
