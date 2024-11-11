import streamlit as st
import requests

# Define the URLs and headers
base_url = "http://localhost:8080/assistant"
non_stream_url = f"{base_url}/chat"
stream_url = f"{base_url}/chatStream"
headers = {
    "Content-Type": "text/event-stream"
}

# Streamlit UI
st.title("Assistant Chat")
st.write("Enter your query below and press Send to receive a response.")

# Option to choose between Streaming and Non-Streaming mode
mode = st.radio("Select response mode:", ("Non-Streaming", "Streaming"))
query = st.text_area("Query", placeholder="Enter your question here...", height=100)

# Function to handle non-streaming response
def handle_non_streaming(query):
    try:
        response = requests.post(non_stream_url, headers=headers, data=query)
        if response.status_code == 200:
            st.write(response.text)
        else:
            st.error(f"Error: Received status code {response.status_code}")
    except requests.exceptions.RequestException as e:
        st.error("Failed to connect to the server.")
        st.exception(e)

# Function to handle streaming response
def handle_streaming(query):
    try:
        # Initiate a streaming post request
        with requests.post(stream_url, headers=headers, data=query, stream=True) as response:
            if response.status_code == 200:
                full_response = ""
                placeholder = st.empty()
                
                # Iterate over the lines in the streaming response
                for line in response.iter_lines():
                    if line:
                        # Decode and process each line from the stream
                        decoded_line = line.decode("utf-8").replace("data:", "").strip()
                        
                        # Process content, handling special characters as in your example
                        if decoded_line.endswith("-"):
                            full_response += decoded_line[:-1]
                        else:
                            full_response += decoded_line + " "
                        
                        # Update the placeholder text with the current response content
                        placeholder.text(full_response)
            else:
                st.error(f"Error: Received status code {response.status_code}")

    except requests.exceptions.RequestException as e:
        st.error("Failed to connect to the server.")
        st.exception(e)

# Send button to trigger the appropriate method
if st.button("Send"):
    if query.strip():
        if mode == "Non-Streaming":
            handle_non_streaming(query)
        else:
            handle_streaming(query)
    else:
        st.warning("Please enter a query before sending.")
